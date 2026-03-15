package pos.pos.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.pos.entities.Supplier;
import pos.pos.repository.SupplierRepository;
import pos.pos.service.SupplierService;

import java.util.List;

@Service
public class SupplierServiceImpl implements SupplierService {

    @Autowired
    private SupplierRepository supplierRepo;

    @Override
    public void updateSupplierAwards(String supplierId, Double purchaseAmount) {
      Supplier supplier = supplierRepo.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        if (Boolean.TRUE.equals(supplier.getIsSpecial())) {
            // Lógica del 11%: El premio se basa en el importe de la compra
            double newEarned = purchaseAmount * 0.11;
            supplier.setTotalPurchasedAmount(supplier.getTotalPurchasedAmount() + purchaseAmount);
            supplier.setRewardsEarned(supplier.getRewardsEarned() + newEarned);

            supplierRepo.save(supplier);
        }
    }

    @Override
    public void registerEmptyBottles(String supplierId, Integer quantity) {
        Supplier supplier = supplierRepo.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        // Si quantity es positivo, nos entregaron mercancía (debemos vacíos)
        // Si quantity es negativo, devolvimos vacíos al proveedor
        supplier.setEmptyBottlesOwed(supplier.getEmptyBottlesOwed() + quantity);

        supplierRepo.save(supplier);
    }

    @Override
    public List<Supplier> getAllSuppliers() {
        return supplierRepo.findAll();
    }
}