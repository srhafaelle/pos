package pos.pos.dto;

import lombok.Data;

@Data
public class CashFlowSummary {
    private Double balanceUsd = 0.0;
    private Double balanceBsCash = 0.0;
    private Double balanceBsDigital = 0.0;
    private Double balanceGold = 0.0;
}