package pos.pos.dto;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MovementDTO {
    private String type;        // "IN" (Ingreso/Venta) o "OUT" (Egreso/Gasto)
    private Double amount;
    private String description; // Ej: "Venta #123" o "Pago Luz"
    private LocalDateTime date;
}