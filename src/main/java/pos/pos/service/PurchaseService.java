package pos.pos.service;



import pos.pos.entities.Purchase;

import java.util.List;

public interface PurchaseService {
    Purchase registerPurchase(Purchase purchase);
    List<Purchase> getPurchasesBySupplier(String supplierId);
}