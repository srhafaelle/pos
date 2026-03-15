package pos.pos.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "adjustments")
@Data
public class InventoryAdjustment {
    @Id
    private String id;

    private String productId;
    private String productName;
    private Integer quantity; // Cantidad retirada

    private String reason; // "VENCIMIENTO", "ROTURA", "CONSUMO_INTERNO", "ROBO", "AJUSTE_INVENTARIO"
    private String comments;

    private String userId; // Quién reportó la merma
    private String userName;

    private LocalDateTime timestamp;
}