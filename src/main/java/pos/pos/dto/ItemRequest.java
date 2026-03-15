package pos.pos.dto;
import lombok.Data;

@Data
public class ItemRequest {
    private String productId;
    private Integer quantity;
    private Double priceUsed;
    private String presentationName;
    private boolean isWholesale; // true = Mayor, false = Detal


    // Precio al que se vendió

}