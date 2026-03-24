package pos.pos.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pos.pos.entities.StockMovement;
import pos.pos.service.StockMovementService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stock-movements")
@CrossOrigin(origins = "*")
public class StockMovementController {

    @Autowired
    private StockMovementService stockMovementService;

    @GetMapping
    public ResponseEntity<List<StockMovement>> getAll() {
        return ResponseEntity.ok(stockMovementService.findAllMovements());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<StockMovement>> getByProduct(@PathVariable String productId) {
        return ResponseEntity.ok(stockMovementService.findByProductId(productId));
    }
}