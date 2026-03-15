package pos.pos.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "expenses")
@Data
public class Expense {
    @Id
    private String id;
    private String description; // Ej: "Pago de transporte", "Almuerzo personal"
    private Double amount;
    private String currencyType; // "USD", "BS", "BS Digital", "ORO"
    private String category;
    private LocalDateTime timestamp;
    private String userId;; // Usuario que registró el gasto
}