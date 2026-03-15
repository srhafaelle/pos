package pos.pos.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pos.pos.entities.CompanyBalance;
import pos.pos.repository.CompanyBalanceRepository;

@Service
public class TreasuryService {

    @Autowired
    private CompanyBalanceRepository balanceRepo;

    // Obtener la caja principal (o crearla si no existe)
    public CompanyBalance getBalance() {
        return balanceRepo.findById("1").orElseGet(() -> {
            CompanyBalance newBalance = new CompanyBalance();
            newBalance.setId("1");
            return balanceRepo.save(newBalance);
        });
    }

    // MÉTODO MÁGICO: Sumar Ingreso por Venta
    @Transactional
    public void addIncomeFromSale(Double usd, Double bsCash, Double bsDigital, Double gold) {
        CompanyBalance balance = getBalance();

        balance.setTotalUsd(balance.getTotalUsd() + (usd != null ? usd : 0.0));
        balance.setTotalBsCash(balance.getTotalBsCash() + (bsCash != null ? bsCash : 0.0));
        balance.setTotalBsDigital(balance.getTotalBsDigital() + (bsDigital != null ? bsDigital : 0.0));
        balance.setTotalGold(balance.getTotalGold() + (gold != null ? gold : 0.0));

        balanceRepo.save(balance);
    }

    // MÉTODO PARA GASTOS / COMPRA DE ORO (Restar dinero)
    @Transactional
    public void subtractExpense(Double amount, String currency) {
        CompanyBalance balance = getBalance();

        switch (currency) {
            case "USD": balance.setTotalUsd(balance.getTotalUsd() - amount); break;
            case "BS_CASH": balance.setTotalBsCash(balance.getTotalBsCash() - amount); break;
            case "BS_DIGITAL": balance.setTotalBsDigital(balance.getTotalBsDigital() - amount); break;
            case "GOLD": balance.setTotalGold(balance.getTotalGold() - amount); break;
        }

        balanceRepo.save(balance);
    }

    // MÉTODO PARA COMPRA DE ORO (Sale dinero, Entra oro)
    @Transactional
    public void processGoldPurchase(Double goldReceived, Double moneyPaid, String currencyPaid) {
        CompanyBalance balance = getBalance();

        // Entra Oro
        balance.setTotalGold(balance.getTotalGold() + goldReceived);

        // Sale Dinero
        switch (currencyPaid) {
            case "USD": balance.setTotalUsd(balance.getTotalUsd() - moneyPaid); break;
            case "BS_CASH": balance.setTotalBsCash(balance.getTotalBsCash() - moneyPaid); break;
            case "BS_DIGITAL": balance.setTotalBsDigital(balance.getTotalBsDigital() - moneyPaid); break;
        }

        balanceRepo.save(balance);
    }

    @Transactional
    public void addIncomeFromShift(Double declaredUsd, Double  declaredBsCash, Double  declaredBsDigital, Double declaredGold){

        CompanyBalance balance = getBalance();

        balance.setTotalUsd(balance.getTotalUsd() + (declaredUsd != null ? declaredUsd : 0.0));
        balance.setTotalBsCash(balance.getTotalBsCash() + (declaredBsCash != null ? declaredBsCash : 0.0));
        balance.setTotalBsDigital(balance.getTotalBsDigital() + (declaredBsDigital != null ? declaredBsCash : 0.0));
        balance.setTotalGold(balance.getTotalGold() + (declaredGold != null ? declaredGold : 0.0));

        balanceRepo.save(balance);

    }
}