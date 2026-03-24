package pos.pos.service.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pos.pos.entities.Product;
import pos.pos.entities.StockMovement;
import pos.pos.repository.ProductRepository;
import pos.pos.service.ProductService;
import pos.pos.service.StockMovementService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private StockMovementService stockMovementService;

    // Carpeta donde se guardarán las imágenes
    private final String UPLOAD_DIR = "uploads/products/";

    @Override
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    @Override
    public Optional<Product> getProductByCode(String code) {
        return productRepo.findByCode(code);
    }

    @Override
    @Transactional
    public Product saveProduct(Product incomingProduct) {

        // ==========================================
        // 1. PROCESAR LA IMAGEN (Aplica para Crear y Editar)
        // ==========================================
        if (incomingProduct.getImageBase64() != null && !incomingProduct.getImageBase64().isEmpty()) {
            try {
                // Asegurar que el directorio exista
                Path uploadPath = Paths.get(UPLOAD_DIR); // Asegúrate de tener esta constante definida en tu clase
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Generar un nombre único para la imagen
                String fileName = UUID.randomUUID().toString() + ".jpg";
                Path filePath = uploadPath.resolve(fileName);

                // Decodificar el Base64 y guardarlo como archivo físico
                byte[] imageBytes = Base64.getDecoder().decode(incomingProduct.getImageBase64());
                Files.write(filePath, imageBytes);

                // Asignar la URL estática al producto y limpiar el Base64
                incomingProduct.setImageUrl("/images/products/" + fileName);
                incomingProduct.setImageBase64(null);

            } catch (IOException e) {
                throw new RuntimeException("Error al guardar la imagen del producto", e);
            }
        } else if (incomingProduct.getId() != null && !incomingProduct.getId().isEmpty()) {
            // ¡SÚPER IMPORTANTE! Si es una edición y NO enviaron foto nueva, conservamos la foto vieja.
            Product existingForImage = productRepo.findById(incomingProduct.getId()).orElse(null);
            if (existingForImage != null) {
                incomingProduct.setImageUrl(existingForImage.getImageUrl());
            }
        }

        // ==========================================
        // 2. PREPARAR LA BITÁCORA
        // ==========================================
        StockMovement.StockMovementBuilder movementBuilder = StockMovement.builder()
                .timestamp(LocalDateTime.now(ZoneId.of("America/Caracas")))
                .adminName("Admin"); // Luego podrías recibir este dato desde el frontend

        // ==========================================
        // 3. LÓGICA: EDICIÓN VS CREACIÓN NUEVA
        // ==========================================
        if (incomingProduct.getId() != null && !incomingProduct.getId().isEmpty()) {

            // --- ES UNA EDICIÓN ---
            Product existing = productRepo.findById(incomingProduct.getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado para editar"));

            // Validar que no le esté robando el código de barras a otro producto
            Optional<Product> existingWithCode = productRepo.findByCode(incomingProduct.getCode());
            if(existingWithCode.isPresent() && !existingWithCode.get().getId().equals(existing.getId())) {
                throw new RuntimeException("Ese código de barras ya pertenece a otro producto.");
            }

            // Guardamos el estado anterior para la bitácora
            Integer oldStock = existing.getStock();
            Double oldPrice = existing.getPriceRetail();

            // Guardar producto
            Product savedProduct = productRepo.save(incomingProduct);

            // Si cambió stock o precio, registrar el movimiento
            if (!oldStock.equals(savedProduct.getStock()) || !oldPrice.equals(savedProduct.getPriceRetail())) {
                movementBuilder.productId(savedProduct.getId())
                        .productName(savedProduct.getName())
                        .backQuantity(oldStock)
                        .newQuantity(savedProduct.getStock())
                        .oldPrice(oldPrice)
                        .newPrice(savedProduct.getPriceRetail());

                stockMovementService.createMovement(movementBuilder.build());
            }
            return savedProduct;

        } else {

            // --- ES UN PRODUCTO NUEVO ---
            // Validar que el código no exista
            if (productRepo.findByCode(incomingProduct.getCode()).isPresent()) {
                throw new RuntimeException("El producto con código " + incomingProduct.getCode() + " ya existe.");
            }

            // Guardar producto
            Product savedProduct = productRepo.save(incomingProduct);

            // Registrar el ingreso inicial en la bitácora
            movementBuilder.id(savedProduct.getId())
                    .productName(savedProduct.getName())
                    .backQuantity(0) // Era 0 porque es nuevo
                    .newQuantity(savedProduct.getStock())
                    .oldPrice(0.0)
                    .newPrice(savedProduct.getPriceRetail());

            stockMovementService.createMovement(movementBuilder.build());

            return savedProduct;
        }
    }

    @Override
    public void deleteProduct(String id) {
        // Opcional: Aquí podrías agregar lógica para buscar el producto y borrar la imagen física antes de borrar el registro
        productRepo.deleteById(id);
    }

    @Override
    public List<Product> getLowStockProducts() {
        return productRepo.findAll().stream()
                .filter(p -> p.getStock() <= (p.getMinStock() != null ? p.getMinStock() : 10))
                .collect(Collectors.toList());
    }
}