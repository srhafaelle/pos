package pos.pos.dto;
import lombok.Data;

@Data
public class CloseShiftRequest {
    private String shiftId;
    private Double declaredUsd;
    private Double declaredBsCash;
    private Double declaredGold;
    private Double declaredBsDigital;
    private String comment;
}