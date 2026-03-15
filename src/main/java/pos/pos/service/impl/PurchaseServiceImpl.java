package pos.pos.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pos.pos.entities.Product;
import pos.pos.entities.Purchase;
import pos.pos.entities.Supplier;
import pos.pos.repository.ProductRepository;
import pos.pos.repository.PurchaseRepository;
import pos.pos.repository.SupplierRepository;
import pos.pos.service.PurchaseService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepo;
    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private SupplierRepository supplierRepo;

    @Override
    @Transactional // Importante: Si algo falla, se reversa todo
    public Purchase registerPurchase(Purchase purchase) {

        // 1. Validar y Actualizar Proveedor
        Supplier supplier = supplierRepo.findById(purchase.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        purchase.setSupplierName(supplier.getName());
        purchase.setDate(LocalDateTime.now());

        // 2. Calcular Deuda
        // El frontend nos manda cuánto pagó (amountPaidUsd). Calculamos el resto.
        double total = purchase.getTotalAmountUsd();
        double paid = purchase.getAmountPaidUsd() != null ? purchase.getAmountPaidUsd() : 0.0;

        double debt = total - paid;
        if (debt < 0) debt = 0.0; // Evitar negativos por error

        purchase.setPendingAmountUsd(debt);

        // Definir estado
        if (debt <= 0.01) { // Margen de error por decimales
            purchase.setStatus("PAID");
        } else if (paid > 0) {
            purchase.setStatus("PARTIAL");
        } else {
            purchase.setStatus("PENDING");
        }

        // 3. Sumar deuda al Proveedor
        if (debt > 0) {
            double currentDebt = supplier.getCurrentBalanceUsd() != null ? supplier.getCurrentBalanceUsd() : 0.0;
            supplier.setCurrentBalanceUsd(currentDebt + debt);
            supplierRepo.save(supplier);
        }

        // 4. Actualizar Inventario (Stock y Costos)
        if (purchase.getItems() != null) {
            for (Purchase.PurchaseItem item : purchase.getItems()) {
                Product product = productRepo.findById(item.getProductId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + item.getProductId()));

                // Aumentar Stock
                int newStock = product.getStock() + item.getQuantity();
                product.setStock(newStock);

                // Opcional: Actualizar el costo de referencia del producto
                // Esto es útil para saber el margen de ganancia futuro
                if (item.getCostPriceUsd() != null && item.getCostPriceUsd() > 0) {
                    // Podrías implementar lógica de costo promedio aquí si quisieras
                    // Por ahora, actualizamos al último costo
                    // product.setCostPrice(item.getCostPriceUsd()); // (Asumiendo que tienes este campo en Product)
                }

                productRepo.save(product);
            }
        }

        return purchaseRepo.save(purchase);
    }

    @Override
    public List<Purchase> getPurchasesBySupplier(String supplierId) {
        return  purchaseRepo.findAll();
    }
}