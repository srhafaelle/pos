package pos.pos.service;


import pos.pos.dto.BalanceResponse;
import pos.pos.entities.Expense;

public interface AccountingService {
    Expense registerExpense(Expense expense);
    BalanceResponse getCurrentBalance(); // Calcula el balance total actual
}