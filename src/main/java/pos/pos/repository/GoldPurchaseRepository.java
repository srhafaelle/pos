package pos.pos.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import pos.pos.entities.GoldPurchase;

import java.time.LocalDateTime;
import java.util.List;

public interface GoldPurchaseRepository extends MongoRepository<GoldPurchase, String> {
    List<GoldPurchase> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}