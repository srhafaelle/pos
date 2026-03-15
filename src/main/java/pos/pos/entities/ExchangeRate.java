package pos.pos.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "exchange_rates")
public class ExchangeRate {
    @Id
    private String id;
    private Double bsRate;      // Cuántos BS por 1 USD
    private Double goldRate;    // Cuántos Gramos de Oro por 1 USD (o viceversa según prefieras)
    private LocalDateTime lastUpdate;
}