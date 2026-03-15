package pos.pos.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pos.pos.entities.ExchangeRate;
import pos.pos.repository.ExchangeRateRepository;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/rates")
@CrossOrigin(origins = "*")
public class ExchangeRateController {

    @Autowired
    private ExchangeRateRepository rateRepo;

    // Obtener tasa actual
    @GetMapping("/current")
    public ResponseEntity<ExchangeRate> getCurrentRate() {
        return rateRepo.findFirstByOrderByLastUpdateDesc()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Actualizar tasa (Solo Admin debería poder hacer esto)
    @PostMapping("/update")
    public ResponseEntity<ExchangeRate> updateRate(@RequestBody ExchangeRate newRate) {
        newRate.setId(null); // Asegurar que sea un registro nuevo
        newRate.setLastUpdate(LocalDateTime.now());
        return ResponseEntity.ok(rateRepo.save(newRate));
    }
}