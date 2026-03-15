package pos.pos.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import pos.pos.entities.Supplier;

import java.util.List;

public interface SupplierRepository extends MongoRepository<Supplier, String> {
    List<Supplier> findByIsSpecialTrue(); // Para obtener rápidamente los que manejan premios
}
