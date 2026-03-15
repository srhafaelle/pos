package pos.pos.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import pos.pos.entities.Shift;

import java.util.List;
import java.util.Optional;

public interface ShiftRepository extends MongoRepository<Shift, String> {

    Optional<Shift> findByCashierIdAndActiveTrue(String cashierId);


    List<Shift> findByCashierIdOrderByStartTimeDesc(String cashierId);


    boolean existsByCashierIdAndActiveTrue(String cashierId);

    Optional<Shift> findFirstByCashierIdAndActiveTrue(String cashierId);
    Optional<Shift> findByActiveTrue();
}
