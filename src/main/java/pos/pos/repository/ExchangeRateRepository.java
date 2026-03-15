package pos.pos.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import pos.pos.entities.ExchangeRate;

import java.util.Optional;

public interface ExchangeRateRepository extends MongoRepository<ExchangeRate, String> {
    Optional<ExchangeRate> findFirstByOrderByLastUpdateDesc();
}
