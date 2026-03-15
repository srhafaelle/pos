package pos.pos.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import pos.pos.entities.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    // Método para buscar por nombre de usuario (útil para el login más adelante)
    Optional<User> findByUsername(String username);
}