package pos.pos.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pos.pos.dto.LoginRequest;
import pos.pos.entities.User;
import pos.pos.repository.UserRepository;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> userOpt = userRepo.findByUsername(request.getUsername());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // ⚠️ NOTA: En producción usaremos BCryptPasswordEncoder aquí
            if (user.getPassword().equals(request.getPassword())) {
                if (!user.isActive()) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario inactivo");
                }
                // Devolvemos el usuario completo (sin la contraseña por seguridad si quisiéramos filtrar)
                return ResponseEntity.ok(user);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
    }
}
