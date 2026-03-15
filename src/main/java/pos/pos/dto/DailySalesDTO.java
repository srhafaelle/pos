package pos.pos.dto;
import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class DailySalesDTO {
    private String dayName; // Ej: "Lunes", "Martes"
    private Double totalUsd;
}