package pos.pos.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pos.pos.entities.Client;
import pos.pos.repository.ClientRepository;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/clients")
@CrossOrigin(origins = "*")
public class ClientController {

    @Autowired
    private ClientRepository clientRepo;

    @GetMapping
    public List<Client> getAllClients() {
        return clientRepo.findAll();
    }

    // Buscar por Cédula/RIF (Útil para venta rápida)
    @GetMapping("/search/{doc}")
    public ResponseEntity<Client> getClientByDoc(@PathVariable String doc) {
        return clientRepo.findByDocument(doc)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Client saveClient(@RequestBody Client client) {
        return clientRepo.save(client);
    }

    @PostMapping("/{id}/pay-debt")
    public ResponseEntity<?> payDebt(@PathVariable String id, @RequestBody Map<String, Double> payload) {
        Double amount = payload.get("amount"); // Monto que está abonando en USD

        return clientRepo.findById(id).map(client -> {
            double newDebt = client.getCurrentDebt() - amount;
            if (newDebt < 0) newDebt = 0.0; // No permitir deuda negativa

            client.setCurrentDebt(newDebt);
            clientRepo.save(client);

            // AQUÍ DEBERÍAS REGISTRAR UN INGRESO EN CAJA (ACCOUNTING)
            // Por simplicidad ahora solo bajamos la deuda.

            return ResponseEntity.ok(client);
        }).orElse(ResponseEntity.notFound().build());
    }
}