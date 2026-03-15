package pos.pos.dto;

import lombok.Data;

@Data
public class BalanceResponse {
    // Total Ingresos (Ventas)
    private Double totalSalesUsd;
    private Double totalSalesBs;
    private Double totalSalesGold;
    private Double totalSalesBsDigital;

    // Total Egresos (Gastos)
    private Double totalExpensesUsd;
    private Double totalExpensesBs;
    private Double totalExpensesGold;
    private Double totalExpensesBsDigital;

    // Balance Neto (Lo que debe haber en caja)
    private Double netBalanceUsd;
    private Double netBalanceBs;
    private Double netBalanceGold;
    private Double netBalanceBsDigital;
}