package pos.pos.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ShiftStatusDTO {
    private String shiftId;
    private String cashierName;
    private boolean active;
    private LocalDateTime startTime;
    private Double currentBalanceUsd;
    private Double currentBalanceBsCash;
    private Double currentBalanceGold;
    // Aquí podrías sumar ventas realizadas durante el turno
}