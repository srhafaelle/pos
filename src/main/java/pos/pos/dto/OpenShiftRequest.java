package pos.pos.dto;
import lombok.Data;

@Data
public class OpenShiftRequest {
    private String cashierId;
    private String cashierName;


    private Double usd;
    private Double bsCash;
    private Double bsDigital;
    private Double gold;

}