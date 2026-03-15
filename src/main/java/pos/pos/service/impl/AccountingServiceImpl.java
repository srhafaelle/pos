package pos.pos.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pos.pos.dto.BalanceResponse;
import pos.pos.entities.CompanyBalance;
import pos.pos.entities.Expense;
import pos.pos.entities.Sale;
import pos.pos.repository.ExpenseRepository;
import pos.pos.repository.SaleRepository;
import pos.pos.service.AccountingService;
import pos.pos.service.TreasuryService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountingServiceImpl implements AccountingService {

    @Autowired
    private SaleRepository saleRepo;

    @Autowired
    private ExpenseRepository expenseRepo;

    @Autowired
    private TreasuryService treasuryService; // <--- Conectamos con la Caja Principal

    @Override
    @Transactional
    public Expense registerExpense(Expense expense) {
        expense.setTimestamp(LocalDateTime.now());

        // 1. Guardar el registro histórico
        Expense savedExpense = expenseRepo.save(expense);

        // 2. IMPACTAR LA CAJA PRINCIPAL (Restar dinero inmediatamente)
        treasuryService.subtractExpense(expense.getAmount(), expense.getCurrencyType());

        return savedExpense;
    }

    @Override
    public BalanceResponse getCurrentBalance() {
        BalanceResponse balance = new BalanceResponse();

        // A. PARA EL BALANCE NETO (LO QUE HAY EN CAJA REALMENTE)
        // Usamos el TreasuryService que es la fuente de la verdad
        CompanyBalance realBalance = treasuryService.getBalance();

        balance.setNetBalanceUsd(realBalance.getTotalUsd());
        balance.setNetBalanceBs(realBalance.getTotalBsCash());
        balance.setNetBalanceBsDigital(realBalance.getTotalBsDigital());
        balance.setNetBalanceGold(realBalance.getTotalGold());

        // B. PARA LOS TOTALES HISTÓRICOS (INGRESOS VS EGRESOS)
        // Aquí sí sumamos todo el historial para reportes estadísticos

        double incomeUsd = 0.0, incomeBsCahs = 0.0, incomeBsDigital = 0.0 , incomeGold = 0.0;
        double expenseUsd = 0.0, expenseBsCash = 0.0 , expenseBsDigital = 0.0, expenseGold = 0.0;

        // Sumar Ventas
        List<Sale> allSales = saleRepo.findAll();
        for (Sale sale : allSales) {
            if (sale.getPayment() != null) {
                incomeUsd += sale.getPayment().getAmountUsd();
                incomeBsCahs += sale.getPayment().getAmountBsCash();
                incomeBsDigital += sale.getPayment().getAmountBsDigital();
                incomeGold += sale.getPayment().getAmountGoldGrams();
            }
        }

        // Sumar Gastos
        List<Expense> allExpenses = expenseRepo.findAll();
        for (Expense expense : allExpenses) {
            // CORREGIDO: Usamos los nombres correctos y agregamos BREAK
            String currency = expense.getCurrencyType(); // Asegúrate que Expense tenga getCurrencyType()
            if (currency == null) continue;

            switch (currency) {
                case "USD":
                    expenseUsd += expense.getAmount();
                    break;
                case "BS_CASH": // Corregido (antes BSCAHS)
                    expenseBsCash += expense.getAmount();
                    break;
                case "BS_DIGITAL": // Corregido (antes BSDIGITAL)
                    expenseBsDigital += expense.getAmount();
                    break; // <--- FALTABA ESTE BREAK (CRÍTICO)
                case "GOLD": // Corregido (antes ORO)
                    expenseGold += expense.getAmount();
                    break;
            }
        }

        // Llenar datos históricos
        balance.setTotalSalesUsd(incomeUsd);
        balance.setTotalSalesBs(incomeBsCahs);
        balance.setTotalSalesBsDigital(incomeBsDigital); // Asegúrate de tener este campo en el DTO
        balance.setTotalSalesGold(incomeGold);

        balance.setTotalExpensesUsd(expenseUsd);
        balance.setTotalExpensesBs(expenseBsCash);
        balance.setTotalExpensesBsDigital(expenseBsDigital); // Asegúrate de tener este campo en el DTO
        balance.setTotalExpensesGold(expenseGold);

        return balance;
    }
}