package pos.pos.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "products")
@Data
public class Product {
    @Id
    private String id;
    private String code; // Código de barras o interno
    private String name;
    private String category;

    // Precios en USD (Moneda base)
    private Double priceRetail; // Detal
    private Double priceWholesale; // Mayor

    private Integer stock;
    private Integer minStock = 10; // Para alertas al administrador

    // NUEVO: Presentaciones Alternas (Cajas, Bultos, Paquetes)
    private List<ProductPresentation> presentations = new ArrayList<>();

    private Boolean isSpecial; // Para la lógica del 11% de premios
    private String unitType; // "Caja", "Botella", "Lata"
    private String categories;
    private String imageBase64; // foto ccodificada del producto
    private String imageUrl;

    @Data
    public static class ProductPresentation {
        private String name;    // Ej: "Caja (36u)" o "Gruesa (10 paq)"
        private Double priceUsd; // El precio de ESA caja (Ej: $20.00)
        private Integer factor;  // Cuántas unidades base trae (Ej: 36)
    }
}
