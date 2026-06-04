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
        try {
            // Llamamos a nuestro nuevo servicio de anulación
            saleService.cancelSale(id);
            return ResponseEntity.ok("Venta anulada con éxito. Stock y dinero devueltos.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno al anular: " + e.getMessage());
        }
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