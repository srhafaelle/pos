package pos.pos.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "purchases")
@Data
public class Purchase {
    @Id
    private String id;

    private String invoiceNumber; // Número de factura física
    private String supplierId;
    private String supplierName;

    private LocalDateTime date;

    // Totales
    private Double totalAmountUsd;    // Total de la factura
    private Double amountPaidUsd;     // Cuanto pagaste al momento
    private Double pendingAmountUsd;  // Cuanto quedó debiendo (total - paid)

    private String status; // "PAID", "PARTIAL", "PENDING"

    private List<PurchaseItem> items; // Qué productos vinieron


    private List<PaymentRecord> payments;

    @Data
    public static class PaymentRecord {
        private String date;         // Fecha del pago
        private Double amountUsd;    // Monto abonado
        private String method;       // "BS", "USD", "ZELLE", etc.
        private String reference;    // Referencia bancaria
        private String imageBase64;  // Foto del comprobante (Opcional)
    }

    @Data
    public static class PurchaseItem {
        private String productId;
        private String productName;
        private Integer quantity;
        private Double costPriceUsd; // Costo unitario al que entró
    }


}