package pos.pos.service;

import pos.pos.entities.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> getAllProducts();
    Optional<Product> getProductByCode(String code);
    Product saveProduct(Product product);
    void deleteProduct(String id);
    List<Product> getLowStockProducts();
}