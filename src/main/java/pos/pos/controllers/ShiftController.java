package pos.pos.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pos.pos.dto.CloseShiftRequest;
import pos.pos.dto.OpenShiftRequest;
import pos.pos.entities.Shift;
import pos.pos.repository.ShiftRepository;
import pos.pos.service.ShiftService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shifts")
@CrossOrigin(origins = "*")
public class ShiftController {

    @Autowired
    private ShiftService shiftService;
    @Autowired
    private ShiftRepository shiftRepo;



    @GetMapping("/check/{cashierId}")
    public ResponseEntity<?> checkActiveShift(@PathVariable String cashierId) {
        Shift activeShift = shiftService.getActiveShift(cashierId);
        if (activeShift != null) {
            return ResponseEntity.ok(activeShift);
        } else {
            return ResponseEntity.noContent().build();
        }
    }


    // Abrir Caja
    @PostMapping("/open")
    public ResponseEntity<?> openShiftC(@RequestBody OpenShiftRequest request) {
        try {
            // Asegúrate de que OpenShiftRequest tenga estos getters (ver archivo abajo)
            Shift newShift = shiftService.openShift(
                    request.getCashierId(),
                    request.getCashierName(),
                    request.getUsd(),
                    request.getBsCash(),
                    request.getBsDigital(), // Nuevo campo
                    request.getGold()
            );
            return ResponseEntity.ok(newShift);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // Cerrar Caja
    @PostMapping("/{id}/close")
    public ResponseEntity<?> closeShift(@PathVariable String id, @RequestBody CloseShiftRequest req) {
        try {
            Shift closedShift = shiftService.closeShift(
                    id,
                    req.getDeclaredUsd(),
                    req.getDeclaredBsCash(),
                    req.getDeclaredBsDigital(), // Nuevo campo
                    req.getDeclaredGold(),
                    req.getComment()
            );
            return ResponseEntity.ok(closedShift);
        } catch (RuntimeException e) {
            // ERROR CORREGIDO: Ahora body(...) acepta String porque usamos ResponseEntity<?>
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/history")
    public ResponseEntity<List<Shift>> getAllShifts() {

        List<Shift> shifts = shiftRepo.findAll(Sort.by(Sort.Direction.DESC, "startTime"));
        return ResponseEntity.ok(shifts);
    }
}