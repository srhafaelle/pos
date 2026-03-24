package pos.pos.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pos.pos.entities.StockMovement;

import java.util.List;

public interface StockMovementRepository extends MongoRepository<StockMovement, String> {
    List<StockMovement> findByProductIdOrderByTimestampDesc(String productId);
}
