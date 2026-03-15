package pos.pos.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import pos.pos.entities.Sale;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends MongoRepository<Sale, String> {
    // Aquí luego buscaremos ventas por cajero para la contabilidad
    List<Sale> findByCashierId(String cashierId);

    // NUEVO MÉTODO PARA EL CIERRE
    List<Sale> findByCashierIdAndTimestampBetween(String cashierId, LocalDateTime start, LocalDateTime end);
    List<Sale> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
