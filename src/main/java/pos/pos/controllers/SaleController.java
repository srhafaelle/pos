package pos.pos.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pos.pos.dto.SaleRequest;
import pos.pos.entities.Sale;
import pos.pos.repository.ProductRepository;
import pos.pos.repository.SaleRepository;
import pos.pos.repository.ShiftRepository;
import pos.pos.service.SaleService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sales")
@CrossOrigin(origins = "*") // Permitir que Flutter Web se conecte
public class SaleController {

    @Autowired
    private SaleService saleService;

    @Autowired
    private SaleRepository saleRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private ShiftRepository shiftRepo;

    @PostMapping("/process")
    public ResponseEntity<?> createSale(@RequestBody SaleRequest request) { // Cambiar <Sale> por <?>
        try {
            Sale newSale = saleService.processSale(request);
            return new ResponseEntity<>(newSale, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // AHORA SÍ ENVIAMOS EL TEXTO DEL ERROR A FLUTTER
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error crítico: " + e.getMessage());
        }
    }
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelSale(@PathVariable String id) {
        return saleRepo.findById(id).map(sale -> {
            if ("CANCELLED".equals(sale.getStatus())) {
                return ResponseEntity.badRequest().body("La venta ya estaba anulada");
            }

            // 1. Marcar como anulada
            sale.setStatus("CANCELLED");
            saleRepo.save(sale);

            // 2. Devolver Stock (Iterar items)
            for (Sale.SaleItem item : sale.getItems()) {
                productRepo.findById(item.getProductId()).ifPresent(product -> {
                    product.setStock(product.getStock() + item.getQuantity());
                    productRepo.save(product);
                });
            }

            // 3. Si fue a crédito, devolver la deuda al cliente
            if (sale.getClientId() != null) {
                // Lógica simple: Si se anuló, se asume que se reversa la deuda generada
                // Esto requeriría saber cuánto fue crédito exacto, por ahora simplificamos:
                // Si tienes lógica de crédito implementada, aquí restarías la deuda.
            }

            return ResponseEntity.ok("Venta anulada y stock restaurado");
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/shift/{shiftId}")
    public ResponseEntity<?> getSalesByShift(@PathVariable String shiftId) {
        return shiftRepo.findById(shiftId).map(shift -> {
            LocalDateTime end = shift.isActive() ? LocalDateTime.now() : shift.getEndTime();
            List<Sale> sales = saleRepo.findByCashierIdAndTimestampBetween(shift.getCashierId(), shift.getStartTime(), end);
            return ResponseEntity.ok(sales);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Sale> getAllSales() {
        return saleRepo.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
    }
}