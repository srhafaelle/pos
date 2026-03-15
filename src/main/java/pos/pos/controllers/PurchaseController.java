package pos.pos.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pos.pos.entities.Purchase;
import pos.pos.entities.Supplier;
import pos.pos.repository.PurchaseRepository;
import pos.pos.repository.SupplierRepository;
import pos.pos.service.PurchaseService;
import pos.pos.service.TreasuryService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/purchases")
@CrossOrigin(origins = "*")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private PurchaseRepository purchaseRepo;
    @Autowired
    private SupplierRepository supplierRepo;
    @Autowired private TreasuryService treasuryService;

    // 1. CREAR COMPRA
    @PostMapping
    @Transactional
    public ResponseEntity<?> createPurchase(@RequestBody Purchase purchase) {
        try {
            // Toda la lógica compleja se delegó al Servicio
            Purchase savedPurchase = purchaseService.registerPurchase(purchase);
            return ResponseEntity.ok(savedPurchase);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // 2. OBTENER HISTORIAL POR PROVEEDOR
    // Esto hace match con: .../api/v1/purchases/supplier/{id}
    @GetMapping("/supplier/{supplierId}")
    public List<Purchase> getPurchasesBySupplier(@PathVariable String supplierId) {
        return purchaseRepo.findBySupplierIdOrderByDateDesc(supplierId);
    }
    // 3. REGISTRAR PAGO (ABONO) A FACTURA
    // Esto hace match con: .../api/v1/purchases/{id}/pay
    @PostMapping("/{id}/pay")
    @Transactional
    public ResponseEntity<?> addPayment(@PathVariable String id, @RequestBody Purchase.PaymentRecord payment) {
        Purchase purchase = purchaseRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        // Validaciones
        double pending = purchase.getPendingAmountUsd() != null ? purchase.getPendingAmountUsd() : 0.0;

        if (payment.getAmountUsd() > pending + 0.01) {
            return ResponseEntity.badRequest().body("El monto excede la deuda pendiente.");
        }

        // Actualizar Factura
        double paid = purchase.getAmountPaidUsd() != null ? purchase.getAmountPaidUsd() : 0.0;
        purchase.setAmountPaidUsd(paid + payment.getAmountUsd());
        purchase.setPendingAmountUsd(pending - payment.getAmountUsd());


        // Actualizar Estado
        if (purchase.getPendingAmountUsd() <= 0.01) {
            purchase.setStatus("PAID");
            purchase.setPendingAmountUsd(0.0);
        } else {
            purchase.setStatus("PARTIAL");
        }

        // Agregar al historial interno de la factura
        if (purchase.getPayments() == null) purchase.setPayments(new ArrayList<>());
        payment.setDate(LocalDateTime.now().toString());
        purchase.getPayments().add(payment);

        purchaseRepo.save(purchase);

        // Actualizar Deuda Global del Proveedor
        Supplier supplier = supplierRepo.findById(purchase.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        double currentBalance = supplier.getCurrentBalanceUsd() != null ? supplier.getCurrentBalanceUsd() : 0.0;
        double newBalance = currentBalance - payment.getAmountUsd();

        if (newBalance < 0) newBalance = 0.0;

        supplier.setCurrentBalanceUsd(newBalance);
        supplierRepo.save(supplier);
        treasuryService.subtractExpense(payment.getAmountUsd(), "USD");
        return ResponseEntity.ok(purchase);
    }
}