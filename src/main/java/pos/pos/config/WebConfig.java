package pos.pos.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path productUploadDir = Paths.get("uploads/products");
        String productUploadPath = productUploadDir.toAbsolutePath().toUri().toString();

        // ASEGURAMOS LA BARRA INCLINADA AL FINAL (MUY IMPORTANTE)
        if (!productUploadPath.endsWith("/")) {
            productUploadPath += "/";
        }

        registry.addResourceHandler("/images/products/**")
                .addResourceLocations(productUploadPath);
    }
}