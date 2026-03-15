package pos.pos.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import pos.pos.entities.Client;

import java.util.Optional;

public interface ClientRepository extends MongoRepository<Client, String> {

    Optional<Client> findByDocument(String document);
}
