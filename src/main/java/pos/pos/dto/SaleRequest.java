package pos.pos.dto;
import lombok.Data;

import java.util.List;

@Data
public class SaleRequest {
    private String cashierId;
    private String clientId;
    private List<ItemRequest> items;


    private Double amountPaidGold;
    private Double amountPaidBsCash;
    private Double amountPaidBsDigital;
    private Double amountPaidUsd;
    private String paymentCurrency; // Moneda en la que se visualiza el cierre (Default: USD)



}