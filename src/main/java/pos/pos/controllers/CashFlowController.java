package pos.pos.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pos.pos.dto.CashFlowSummary;
import pos.pos.dto.MovementDTO;
import pos.pos.entities.*;
import pos.pos.repository.ExpenseRepository;
import pos.pos.repository.GoldPurchaseRepository;
import pos.pos.repository.SaleRepository;
import pos.pos.repository.ShiftRepository;
import pos.pos.service.TreasuryService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/cash-flow")
@CrossOrigin(origins = "*")
public class CashFlowController {

    @Autowired private ShiftRepository shiftRepo;
    @Autowired private SaleRepository saleRepo;
    @Autowired private ExpenseRepository expenseRepo;
    @Autowired private GoldPurchaseRepository goldRepo;
    @Autowired
    private TreasuryService treasuryService;

    // 1. Obtener Saldos de las 4 Cajas (Del turno activo)
    @GetMapping("/summary")
    public ResponseEntity<CashFlowSummary> getSummary() {

        CompanyBalance balance = treasuryService.getBalance();
        CashFlowSummary summary = new CashFlowSummary();
        summary.setBalanceUsd(balance.getTotalUsd());
        summary.setBalanceBsCash(balance.getTotalBsCash());
        summary.setBalanceBsDigital(balance.getTotalBsDigital());
        summary.setBalanceGold(balance.getTotalGold());

        return ResponseEntity.ok(summary);
    }

    // 2. Obtener Historial de una Caja Específica
    @GetMapping("/history/{currency}")
    public List<MovementDTO> getHistory(@PathVariable String currency) {
        Optional<Shift> activeShiftOpt = shiftRepo.findAll().stream().filter(Shift::isActive).findFirst();
        List<MovementDTO> movements = new ArrayList<>();

        if (activeShiftOpt.isPresent()) {
            Shift shift = activeShiftOpt.get();
            LocalDateTime start = shift.getStartTime();
            LocalDateTime end = LocalDateTime.now();

            // Buscar Ventas (IN)
            List<Sale> sales = saleRepo.findByTimestampBetween(start, end);
            for (Sale s : sales) {
                double amount = 0.0;
                // Filtrar solo si la venta tuvo ingreso en esta moneda
                if (s.getPayment() == null) continue;

                switch (currency) {
                    case "USD": amount = s.getPayment().getAmountUsd(); break;
                    case "GOLD": amount = s.getPayment().getAmountGoldGrams(); break;
                    case "BS_CASH": amount = s.getPayment().getAmountBsCash(); break;
                    case "BS_DIGITAL": amount = s.getPayment().getAmountBsDigital(); break;
                }

                if (amount > 0) {
                    MovementDTO m = new MovementDTO();
                    m.setType("IN");
                    m.setAmount(amount);
                    m.setDescription("Venta #" + s.getId().substring(0, 6)); // O nombre cliente
                    m.setDate(s.getTimestamp());
                    movements.add(m);
                }
            }

            // Buscar Gastos (OUT)
            List<Expense> expenses = expenseRepo.findByCurrencyTypeAndTimestampBetween(currency, start, end);
            for (Expense e : expenses) {
                MovementDTO m = new MovementDTO();
                m.setType("OUT");
                m.setAmount(e.getAmount());
                m.setDescription(e.getDescription() + " (" + e.getCategory() + ")");
                m.setDate(e.getTimestamp());
                movements.add(m);
            }
        }

        // Ordenar por fecha descendente (lo más nuevo arriba)
        movements.sort(Comparator.comparing(MovementDTO::getDate).reversed());
        return movements;
    }

    // 3. Registrar Gasto
    @PostMapping("/expense")
    public Expense registerExpense(@RequestBody Expense expense) {
        expense.setTimestamp(LocalDateTime.now());
        // IMPACTO EN CAJA PRINCIPAL (RESTA)
        treasuryService.subtractExpense(expense.getAmount(), expense.getCurrencyType());

        return expenseRepo.save(expense);
    }

    // 1. NUEVO ENDPOINT: Registrar Compra de Oro
    @PostMapping("/buy-gold")
    public GoldPurchase buyGold(@RequestBody GoldPurchase purchase) {
        purchase.setTimestamp(LocalDateTime.now());
        // IMPACTO EN CAJA PRINCIPAL (SALE DINERO, ENTRA ORO)
        treasuryService.processGoldPurchase(
                purchase.getGrams(),
                purchase.getAmountPaid(),
                purchase.getCurrencyPaid()
        );

        return goldRepo.save(purchase);
    }
}