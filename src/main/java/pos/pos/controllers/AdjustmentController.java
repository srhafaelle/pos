package pos.pos.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pos.pos.entities.InventoryAdjustment;
import pos.pos.entities.Product;
import pos.pos.repository.InventoryAdjustmentRepository;
import pos.pos.repository.ProductRepository;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/adjustments")
@CrossOrigin(origins = "*")
public class AdjustmentController {

    @Autowired
    private InventoryAdjustmentRepository adjustmentRepo;

    @Autowired
    private ProductRepository productRepo;

    @PostMapping
    public ResponseEntity<?> createAdjustment(@RequestBody InventoryAdjustment adjustment) {
        // 1. Validar producto
        Product product = productRepo.findById(adjustment.getProductId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // 2. Verificar que haya suficiente stock para restar (Opcional, a veces hay que ajustar a negativo si hubo error)
        // Por seguridad, no dejaremos stock negativo por mermas
        if (product.getStock() < adjustment.getQuantity()) {
            return ResponseEntity.badRequest().body("No hay suficiente stock para realizar este ajuste.");
        }

        // 3. Actualizar Stock
        product.setStock(product.getStock() - adjustment.getQuantity());
        productRepo.save(product);

        // 4. Guardar Registro
        adjustment.setProductName(product.getName());
        adjustment.setTimestamp(LocalDateTime.now());

        InventoryAdjustment saved = adjustmentRepo.save(adjustment);
        return ResponseEntity.ok(saved);
    }
}