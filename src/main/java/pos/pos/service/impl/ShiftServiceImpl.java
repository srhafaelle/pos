package pos.pos.service.impl;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.pos.entities.GoldPurchase;
import pos.pos.entities.Sale;
import pos.pos.entities.Shift;
import pos.pos.repository.ExpenseRepository;
import pos.pos.repository.GoldPurchaseRepository;
import pos.pos.repository.SaleRepository;
import pos.pos.repository.ShiftRepository;
import pos.pos.service.ShiftService;
import pos.pos.service.TreasuryService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
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
        shift.setStartTime(LocalDateTime.now(ZoneOffset.ofHours(-4)));
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

        // Usamos la zona horaria correcta de Caracas uniformemente
        LocalDateTime now = LocalDateTime.now(ZoneId.of("America/Caracas"));
        shift.setEndTime(now);
        shift.setActive(false);

        // 1. Guardar lo que contó el cajero físicamente
        shift.setDeclaredUsd(declaredUsd);
        shift.setDeclaredBsCash(declaredBsCash);
        shift.setDeclaredBsDigital(declaredBsDigital);
        shift.setDeclaredGold(declaredGold);
        shift.setCloseComment(comment);

        // 2. CALCULAR LO ESPERADO (Este método ahora hace todo: Ventas + Oro)
        calcularEsperadosAlVuelo(shift, now);

        // 3. Enviar a tesorería los montos declarados
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
        Shift shift = shiftRepo.findByCashierIdAndActiveTrue(cashierId).orElse(null);

        if (shift != null) {
            // Calculamos la matemática del sistema al vuelo para que Flutter la muestre
            calcularEsperadosAlVuelo(shift, LocalDateTime.now(ZoneId.of("America/Caracas")));
        }

        return shift;

    }

    private void calcularEsperadosAlVuelo(Shift shift, LocalDateTime endTime) {
        // A. Inicio
        double sysUsd = shift.getStartUsd() != null ? shift.getStartUsd() : 0.0;
        double sysBsCash = shift.getStartBsCash() != null ? shift.getStartBsCash() : 0.0;
        double sysBsDigital = shift.getStartBsDigital() != null ? shift.getStartBsDigital() : 0.0;
        double sysGold = shift.getStartGold() != null ? shift.getStartGold() : 0.0;

        // B. Sumar Ventas
        List<Sale> sales = saleRepo.findByCashierIdAndTimestampBetween(shift.getCashierId(), shift.getStartTime(), endTime);
        for (Sale s : sales) {
            if (s.getPayment() != null) {
                sysUsd += s.getPayment().getAmountUsd() != null ? s.getPayment().getAmountUsd() : 0.0;
                sysBsCash += s.getPayment().getAmountBsCash() != null ? s.getPayment().getAmountBsCash() : 0.0;
                sysBsDigital += s.getPayment().getAmountBsDigital() != null ? s.getPayment().getAmountBsDigital() : 0.0;
                sysGold += s.getPayment().getAmountGoldGrams() != null ? s.getPayment().getAmountGoldGrams() : 0.0;
            }
        }

        // C. Procesar Compras de Oro (Sale Dinero, Entra Oro)
        List<GoldPurchase> goldBuys = goldRepo.findByTimestampBetween(shift.getStartTime(), endTime);
        for (GoldPurchase gp : goldBuys) {
            // Entra Oro
            sysGold += gp.getGrams();

            // Sale Dinero
            if ("USD".equals(gp.getCurrencyPaid())) {
                sysUsd -= gp.getAmountPaid();
            } else if ("BS_CASH".equals(gp.getCurrencyPaid())) {
                sysBsCash -= gp.getAmountPaid();
            } else if ("BS_DIGITAL".equals(gp.getCurrencyPaid())) {
                sysBsDigital -= gp.getAmountPaid();
            }
        }

        // Asignamos los resultados
        shift.setExpectedAmountUsd(sysUsd);
        shift.setExpectedBsCash(sysBsCash);
        shift.setExpectedBsDigital(sysBsDigital);
        shift.setExpectedAmountGold(sysGold);
    }

}

