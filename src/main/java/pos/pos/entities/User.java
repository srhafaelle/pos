package pos.pos.entities;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Set;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String username;
    private String password;
    private String fullName;
    private Set<String> roles; // ROLE_ADMIN, ROLE_CASHIER, ROLE_WAREHOUSE
    private boolean active;
}