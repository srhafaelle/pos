package pos.pos.dto;
import lombok.Data;

@Data
public class RewardUpdateResponse {
    private String supplierName;
    private Double pendingRewards;
    private Integer emptyBottlesOwed;
}