package pos.pos.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import pos.pos.entities.Expense;

import java.time.LocalDateTime;
import java.util.List;

public interface ExpenseRepository extends MongoRepository<Expense, String> {
    List<Expense> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<Expense> findByCurrencyTypeAndTimestampBetween(String currency, LocalDateTime start, LocalDateTime end);

}