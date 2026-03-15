package pos.pos.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "company_balance")
@Data
public class CompanyBalance {
    @Id
    private String id; // Siempre será "1"

    // SALDOS ACUMULADOS REALES
    private Double totalUsd = 0.0;
    private Double totalBsCash = 0.0;
    private Double totalBsDigital = 0.0;
    private Double totalGold = 0.0;


    public double funcion(){
        return  totalBsCash;
    }
}