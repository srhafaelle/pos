package pos.pos.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class TopProductDTO {
    private String productName;
    private Integer quantitySold;
    private Double totalRevenueUsd;
}
