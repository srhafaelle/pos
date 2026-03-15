package pos.pos.controllers;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pos.pos.entities.User;
import pos.pos.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepo;

    // Listar todos los empleados
    @GetMapping
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    // Crear o Editar Empleado
    @PostMapping
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        // Validación básica: Si es nuevo, verificar que el username no exista
        if (user.getId() == null && userRepo.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().build(); // Usuario ya existe
        }

        // ⚠️ NOTA: En producción aquí encriptaríamos la contraseña con BCrypt
        user.setActive(true);
        return ResponseEntity.ok(userRepo.save(user));
    }

    // Eliminar (o desactivar) empleado
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}