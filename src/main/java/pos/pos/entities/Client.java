package pos.pos.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "clients")
@Data
public class Client {
    @Id
    private String id;
    private String name;      // Nombre o Razón Social
    private String document;  // Cédula o RIF (Ej: V-12345678)
    private String phone;
    private String address;
    private String email;
    private Double currentDebt = 0.0;



}