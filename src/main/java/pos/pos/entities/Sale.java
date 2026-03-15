package pos.pos.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "sales")
@Data
public class Sale {
    @Id
    private String id;
    private LocalDateTime timestamp;
    private String cashierId; // Corregido: antes decía userId, ahora coincide con el Service

    private List<SaleItem> items; // Lista de items vendidos (Snapshot)

    private Double totalUsd; // Total neto en dólares
    private Double tax;
    private Double discount;
    private String clientId;    // ID del cliente (puede ser null si es anónimo)
    private String clientName;  // Nombre para redundancia en reportes
    private String clientDoc;   // RIF/CI para la factura
    private String status = "ACTIVE";
    // Desglose del pago (Multimoneda)
    private PaymentDetail payment;


    // --- Clases Internas para Estructura del Documento ---

    @Data
    public static class SaleItem {
        private String productId;
        private String productName;
        private Integer quantity;
        private Double unitPrice; // Precio al momento de la venta
        private Double subTotal;

    }

    @Data
    public static class PaymentDetail {
        private Double amountUsd;
        private Double amountGoldGrams;
        private Double exchangeRateBs;   // Tasa usada en ese momento
        private Double exchangeRateGold; // Tasa usada en ese momento
        // SEPARAMOS LOS BS
        private Double amountBsCash;    // Efectivo
        private Double amountBsDigital; // Pago Móvil / Punto
        private String method;
    }
}