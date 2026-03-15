package pos.pos.service.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.pos.entities.Product;
import pos.pos.repository.ProductRepository;
import pos.pos.service.ProductService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepo;

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
    public Product saveProduct(Product product) {
        // Verificar si viene una imagen en Base64 desde Flutter
        Path filePath = null;
        if (product.getImageBase64() != null && !product.getImageBase64().isEmpty()) {
            try {
                // Asegurar que el directorio exista
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Generar un nombre único para la imagen
                String fileName = UUID.randomUUID().toString() + ".jpg";
                filePath = uploadPath.resolve(fileName);

                // Decodificar el Base64 y guardarlo como archivo físico
                byte[] imageBytes = Base64.getDecoder().decode(product.getImageBase64());
                Files.write(filePath, imageBytes);

                // Asignar la URL estática al producto y limpiar el Base64 para no guardarlo en Mongo
                product.setImageUrl("/images/products/" + fileName);
                product.setImageBase64(null);

            } catch (IOException e) {
                throw new RuntimeException("Error al guardar la imagen del producto", e);
            }
        }

        return productRepo.save(product);
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