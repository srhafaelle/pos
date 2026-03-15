package pos.pos.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "gold_purchases")
@Data
public class GoldPurchase {
    @Id
    private String id;

    private Double grams;          // Cantidad de oro que entra (gr)
    private Double amountPaid;     // Dinero que sale
    private String currencyPaid;   // Moneda usada: "USD", "BS_CASH", "BS_DIGITAL"
    private Double exchangeRate;   // Tasa usada en el momento

    private LocalDateTime timestamp;
    private String cashierId;      // Quién hizo la compra
    private String description;    // Opcional: Nombre del cliente / minero
}