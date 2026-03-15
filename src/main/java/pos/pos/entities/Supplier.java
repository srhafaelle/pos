package pos.pos.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "suppliers")
@Data
public class Supplier {
    @Id
    private String id;
    private String name;
    private String contactName;
    private String phone;
    private String rif;
    private Boolean isSpecial; // El que maneja premios del 11%
    private Double currentBalanceUsd = 0.0;

    // Control de Envases (Vacíos)
    private Integer emptyBottlesOwed = 0;

    // Lógica de Premios
    private Double totalPurchasedAmount = 0.0;
    private Double rewardsEarned = 0.0;
    private Double rewardsDelivered = 0.0;


    // Este método es calculado, no se guarda en BD, pero sirve para el Frontend
    public Double getRewardsPending() {
        double earned = rewardsEarned != null ? rewardsEarned : 0.0;
        double delivered = rewardsDelivered != null ? rewardsDelivered : 0.0;
        return earned - delivered;
    }
}