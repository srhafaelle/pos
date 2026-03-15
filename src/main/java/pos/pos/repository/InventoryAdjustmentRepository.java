package pos.pos.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import pos.pos.entities.InventoryAdjustment;

public interface InventoryAdjustmentRepository extends MongoRepository<InventoryAdjustment, String> {
}
