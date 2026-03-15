package pos.pos.service;




import pos.pos.entities.Supplier;

import java.util.List;

public interface SupplierService {
    void updateSupplierAwards(String supplierId, Double purchaseAmount);
    void registerEmptyBottles(String supplierId, Integer quantity);
    List<Supplier> getAllSuppliers();
}