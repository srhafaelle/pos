package pos.pos.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "config")
@Data
public class AppConfig {
    @Id
    private String id; // Siempre usaremos el mismo ID fijo, ej: "1"

    private String businessName;
    private String rif;
    private String address;
    private String phone;
    private String ticketFooterMessage;

    // Tasa de Impuesto (IVA/IGTF) si quisieras aplicarlo a futuro
    private Double taxPercentage;
}