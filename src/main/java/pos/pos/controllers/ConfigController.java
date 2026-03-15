package pos.pos.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pos.pos.entities.AppConfig;
import pos.pos.repository.AppConfigRepository;

@RestController
@RequestMapping("/api/v1/config")
@CrossOrigin(origins = "*")
public class ConfigController {

    @Autowired
    private AppConfigRepository configRepo;

    @GetMapping
    public AppConfig getConfig() {
        // Retornar la config existente o una por defecto si está vacía
        return configRepo.findById("1").orElseGet(() -> {
            AppConfig def = new AppConfig();
            def.setId("1");
            def.setBusinessName("MI NEGOCIO");
            def.setRif("J-00000000");
            def.setAddress("Venezuela");
            def.setTicketFooterMessage("Gracias por su compra");
            return configRepo.save(def);
        });
    }

    @PostMapping
    public AppConfig updateConfig(@RequestBody AppConfig config) {
        config.setId("1"); // Forzar que siempre sea el ID 1 para sobrescribir
        return configRepo.save(config);
    }
}
