package pos.pos.service.impl;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.pos.entities.Sale;
import pos.pos.entities.Shift;
import pos.pos.repository.ExpenseRepository;
import pos.pos.repository.GoldPurchaseRepository;
import pos.pos.repository.SaleRepository;
import pos.pos.repository.ShiftRepository;
import pos.pos.service.ShiftService;
import pos.pos.service.TreasuryService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShiftServiceImpl implements ShiftService {
    @Autowired private ShiftRepository shiftRepo;
    @Autowired private SaleRepository saleRepo;
    @Autowired
    private ExpenseRepository expenseRepo;
    @Autowired private GoldPurchaseRepository goldRepo;
    @Autowired private TreasuryService treasuryService;


    @Override
    public Shift openShift(String cashierId, String cashierName, Double startUsd, Double startBs, Double startBsDigital, Double startGold) {

        if (shiftRepo.existsByCashierIdAndActiveTrue(cashierId)) {
            throw new RuntimeException("El cajero ya tiene un turno abierto.");

        }


        Shift shift = new Shift();
        shift.setCashierId(cashierId);
        shift.setCashierName(cashierName);
        shift.setActive(true);
        shift.setStartTime(LocalDateTime.now());
        shift.setStartUsd(startUsd != null ? startUsd : 0.0);
        shift.setStartBsCash(startBs != null ? startBs : 0.0);
        shift.setStartBsDigital( startBsDigital != null ? startBsDigital : 0.0);
        shift.setStartGold(startGold != null ? startGold : 0.0);
        return shiftRepo.save(shift);

    }



    @Override

    public Shift closeShift(String shiftId, Double declaredUsd, Double declaredBsCash, Double declaredBsDigital, Double declaredGold, String comment) {
        Shift shift = shiftRepo.findById(shiftId)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));
        if (!shift.isActive()) {
            throw new RuntimeException("El turno ya está cerrado.");
        }
        LocalDateTime now = LocalDateTime.now();
        shift.setEndTime(now);
        shift.setActive(false);

// 1. Guardar lo que contó el cajero
        shift.setDeclaredUsd(declaredUsd);
        shift.setDeclaredBsCash(declaredBsCash);
        shift.setDeclaredBsDigital(declaredBsDigital);
        shift.setDeclaredGold(declaredGold);
        shift.setCloseComment(comment);
// 2. CALCULAR LO ESPERADO (MATEMÁTICA DEL SISTEMA)
// A. Inicio

        double sysUsd = shift.getStartUsd();
        double sysBsCash = shift.getStartBsCash();
        double sysBsDigital = 0.0; // Siempre inicia en 0 relativo al turno
        double sysGold = shift.getStartGold();

// B. Sumar Ventas (INGRESOS)

        List<Sale> sales = saleRepo.findByCashierIdAndTimestampBetween(shift.getCashierId(), shift.getStartTime(), now);
        for (Sale s : sales) {
            if (s.getPayment() != null) {
                sysUsd += s.getPayment().getAmountUsd();
                sysBsCash += s.getPayment().getAmountBsCash();
                sysBsDigital += s.getPayment().getAmountBsDigital();
                sysGold += s.getPayment().getAmountGoldGrams();
            }
        }

        /* logica migrare a la caja principal tesoreria por tema de gastos el turno no maneja ningun tipo de gastos
// C. Restar Gastos (EGRESOS)

        List<Expense> expenses = expenseRepo.findByTimestampBetween(shift.getStartTime(), now);
        for (Expense e : expenses) {
            switch (e.getCurrencyType()) { // Asegúrate que en Expense sea currencyType
                case "USD": sysUsd -= e.getAmount(); break;
                case "BS_CASH": sysBsCash -= e.getAmount(); break;
                case "BS_DIGITAL": sysBsDigital -= e.getAmount(); break;
                case "GOLD": sysGold -= e.getAmount(); break;

            }

        }

// D. Procesar Compra de Oro (SALE DINERO, ENTRA ORO)
        List<GoldPurchase> goldBuys = goldRepo.findByTimestampBetween(shift.getStartTime(), now);
        for (GoldPurchase gp : goldBuys) {
// Entra Oro
            sysGold += gp.getGrams();

// Sale Dinero
            switch (gp.getCurrencyPaid()) {
                case "USD": sysUsd -= gp.getAmountPaid(); break;
                case "BS_CASH": sysBsCash -= gp.getAmountPaid(); break;
                case "BS_DIGITAL": sysBsDigital -= gp.getAmountPaid(); break;
            }
        }

// 3. Guardar Esperados (Evitar negativos visuales)
        shift.setExpectedAmountUsd(Math.max(0, sysUsd));
        shift.setExpectedBsCash(Math.max(0, sysBsCash));
        shift.setExpectedBsDigital(Math.max(0, sysBsDigital));
        shift.setExpectedAmountGold(Math.max(0, sysGold));
*/
       treasuryService.addIncomeFromShift(
                declaredUsd,
                declaredBsCash,
                declaredBsDigital,
                declaredGold
        );

        return shiftRepo.save(shift);

    }

  @Override
  public Shift cajaDeTurno (String shiftId, Double usd, Double bsCash, Double bsDigitals, Double oro){
      Shift turno = shiftRepo.findById(shiftId)
              .orElseThrow(()-> new RuntimeException("turno no encontrado: " + shiftId));

      if(turno.isActive()){
          // Usamos una sintaxis más segura para evitar nulos (NullPointerException) si las variables son nulas inicialmente
          double currentUsd = turno.getExpectedAmountUsd() != null ? turno.getExpectedAmountUsd() : 0.0;
          double currentBsCash = turno.getExpectedBsCash() != null ? turno.getExpectedBsCash() : 0.0;
          double currentBsDigital = turno.getExpectedBsDigital() != null ? turno.getExpectedBsDigital() : 0.0;
          double currentGold = turno.getExpectedAmountGold() != null ? turno.getExpectedAmountGold() : 0.0;

          // Sumamos los valores seguros
          turno.setExpectedAmountUsd(currentUsd + (usd != null ? usd : 0.0));
          turno.setExpectedBsCash(currentBsCash + (bsCash != null ? bsCash : 0.0));
          turno.setExpectedBsDigital(currentBsDigital + (bsDigitals != null ? bsDigitals : 0.0));
          turno.setExpectedAmountGold(currentGold + (oro != null ? oro : 0.0));

          // IMPORTANTE: Guardamos el turno actualizado
          shiftRepo.save(turno);
      }
       return turno;

  }

    @Override
    public Shift getActiveShift(String cashierId) {
        return shiftRepo.findByCashierIdAndActiveTrue(cashierId).orElse(null);

    }

}

