package pos.pos.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import pos.pos.entities.CompanyBalance;

public interface CompanyBalanceRepository extends MongoRepository<CompanyBalance, String> {}
