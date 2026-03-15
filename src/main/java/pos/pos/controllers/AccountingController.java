package pos.pos.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pos.pos.dto.BalanceResponse;
import pos.pos.entities.Expense;
import pos.pos.repository.ExpenseRepository;
import pos.pos.service.AccountingService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounting")
@CrossOrigin(origins = "*")
public class AccountingController {

    @Autowired
    private AccountingService accountingService;
    @Autowired
    private ExpenseRepository expenseRepo;

    // Ver el estado de la caja (Dashboard Admin)
    @GetMapping("/balance")
    public ResponseEntity<BalanceResponse> getBalance() {
        return ResponseEntity.ok(accountingService.getCurrentBalance());
    }

    // Registrar un gasto (Salida de dinero)
    @PostMapping("/expenses")
    public ResponseEntity<Expense> registerExpense(@RequestBody Expense expense) {
        return ResponseEntity.ok(accountingService.registerExpense(expense));
    }

    // Ver historial de gastos
    @GetMapping("/expenses")
    public List<Expense> getAllExpenses() {
        // Idealmente ordenar por fecha descendente
        return expenseRepo.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
    }
}