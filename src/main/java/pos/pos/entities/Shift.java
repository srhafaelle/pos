package pos.pos.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "shifts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shift {
    @Id
    private String id;

    private String cashierId;
    private String cashierName;
    private boolean active;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // --- FONDOS INICIALES (Con lo que arrancó) ---
    // Fondos Iniciales
    private Double startUsd;
    private Double startBsCash;
    private Double startBsDigital;
    private Double startGold;



    // --- ESPERADO (Lo que el sistema calcula: Inicio + Ventas - Gastos - Compras Oro) ---
    private Double expectedAmountUsd;
    private Double expectedBsCash;
    private Double expectedBsDigital;
    private Double expectedAmountGold;

    // Montos Declarados (Al cerrar)
    private Double declaredUsd;
    private Double declaredBsCash;
    private Double declaredBsDigital;
    private Double declaredGold;

    // --- COMENTARIOS ---
    private String closeComment;
}