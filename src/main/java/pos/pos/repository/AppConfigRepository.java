package pos.pos.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import pos.pos.entities.AppConfig;

public interface AppConfigRepository extends MongoRepository<AppConfig, String> {
}
