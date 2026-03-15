package pos.pos.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import pos.pos.entities.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {
    Optional<Product> findByCode(String code);
    List<Product> findByStockLessThan(Integer amount);
}
