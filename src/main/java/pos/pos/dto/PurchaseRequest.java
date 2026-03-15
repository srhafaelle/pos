package pos.pos.dto;
import lombok.Data;

@Data

public class PurchaseRequest {
    private String supplierId;
    private String productId;
    private Integer quantity; // Cantidad de cajas/bultos
    private Double totalCost; // Cuánto costó la factura total (Base para el 11%)
    private Integer emptyBottles; // Vacíos que entregamos o recibimos


}