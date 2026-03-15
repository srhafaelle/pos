package pos.pos.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import pos.pos.entities.Purchase;

import java.util.List;

public interface PurchaseRepository extends MongoRepository<Purchase, String> {

    List<Purchase> findBySupplierIdOrderByDateDesc(String supplierId);
}
