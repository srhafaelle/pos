package pos.pos.service;


import pos.pos.entities.Shift;

public interface ShiftService {
    Shift openShift(String cashierId, String cashierName, Double startUsd, Double startBs, Double startBsDigital, Double startGold);
    Shift closeShift(String shiftId, Double declaredUsd, Double declaredBsCash, Double declaredBsDigital, Double declaredGold, String comment);
    Shift getActiveShift(String cashierId);
    Shift cajaDeTurno(String id, Double u, Double b, Double d, Double o);
}
