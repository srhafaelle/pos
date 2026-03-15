package pos.pos.service.impl;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pos.pos.dto.ItemRequest;
import pos.pos.dto.SaleRequest;
import pos.pos.entities.Product;
import pos.pos.entities.Sale;
import pos.pos.entities.Shift;
import pos.pos.repository.ProductRepository;
import pos.pos.repository.SaleRepository;
import pos.pos.service.SaleService;
import pos.pos.service.ShiftService;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class SaleServiceImpl implements SaleService {

    @Autowired
    private ProductRepository productRepo;
    @Autowired private SaleRepository saleRepo;
    @Autowired private ShiftService shiftService;



    @Override
    @Transactional
    public Sale processSale(SaleRequest request) {
        // 1. Validar Turno Activo
        Shift activeShift = shiftService.getActiveShift(request.getCashierId());
        if (activeShift == null) {
            throw new RuntimeException("No hay turno abierto para este cajero.");
        }

        // 2. Crear Objeto Venta
        Sale sale = new Sale();
        sale.setCashierId(request.getCashierId());
        sale.setClientId(request.getClientId());
        sale.setTimestamp(LocalDateTime.now());
        sale.setItems(new ArrayList<>());

        double totalSaleAmount = 0.0;

        // 3. Procesar Productos y Descontar Stock
        for (ItemRequest itemReq : request.getItems()) {
            Product product = productRepo.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + itemReq.getProductId()));

            int conversorUnidadAlterna = 1;
            String nombrePresentacion = itemReq.getPresentationName();

            if(nombrePresentacion !=null && !nombrePresentacion.isEmpty() && product.getPresentations() != null){
                for (Product.ProductPresentation p: product.getPresentations()) {
                    if (p.getName().equalsIgnoreCase(nombrePresentacion)) {
                        conversorUnidadAlterna = p.getFactor(); // Ej: 24 (Caja)
                        break;
                    }
                }

            }

            int totalUnidadesADescontar = itemReq.getQuantity() * conversorUnidadAlterna;

            if (product.getStock() < itemReq.getQuantity()) {
                throw new RuntimeException("Stock insuficiente para: " + product.getName());
            }

            // Descontar Stock
            product.setStock(product.getStock() - totalUnidadesADescontar);
            productRepo.save(product);

            // Agregar a la venta
            Sale.SaleItem saleItem = new Sale.SaleItem();
            saleItem.setProductId(product.getId());
            saleItem.setProductName(product.getName());
            saleItem.setQuantity(itemReq.getQuantity());
            saleItem.setUnitPrice(itemReq.getPriceUsed());
            saleItem.setSubTotal(itemReq.getPriceUsed() * itemReq.getQuantity());

            sale.getItems().add(saleItem);
            totalSaleAmount += saleItem.getSubTotal();
        }

        sale.setTotalUsd(totalSaleAmount);

        // 4. Procesar Pagos
        Sale.PaymentDetail payment = new Sale.PaymentDetail();

        // Usamos los nombres exactos definidos en tu DTO SaleRequest
        payment.setAmountUsd(request.getAmountPaidUsd() != null ? request.getAmountPaidUsd() : 0.0);
        payment.setAmountBsCash(request.getAmountPaidBsCash() != null ? request.getAmountPaidBsCash() : 0.0);
        payment.setAmountBsDigital(request.getAmountPaidBsDigital() != null ? request.getAmountPaidBsDigital() : 0.0);
        payment.setAmountGoldGrams(request.getAmountPaidGold() != null ? request.getAmountPaidGold() : 0.0);

        // CORRECCIÓN DEL ERROR DE SINTAXIS: Era setMethod, no set
        payment.setMethod(determineMainMethod(payment));
        sale.setPayment(payment);

        Sale savedSale = saleRepo.save(sale);


        shiftService.cajaDeTurno(activeShift.getId(), request.getAmountPaidUsd(), request.getAmountPaidBsCash(), request.getAmountPaidBsDigital(), request.getAmountPaidGold());
        System.out.println(sale.getDiscount());
        return savedSale;
    }

    private String determineMainMethod(Sale.PaymentDetail p) {
        if (p.getAmountGoldGrams() > 0 && p.getAmountUsd() == 0 && p.getAmountBsCash() == 0) return "ORO";
        if (p.getAmountBsDigital() > 0 && p.getAmountUsd() == 0) return "PAGO_MOVIL";
        if (p.getAmountBsCash() > 0 && p.getAmountUsd() == 0) return "BS_EFECTIVO";
        if (p.getAmountUsd() > 0 && p.getAmountBsCash() == 0 && p.getAmountBsDigital() == 0) return "USD";
        return "MIXTO";
    }
}
