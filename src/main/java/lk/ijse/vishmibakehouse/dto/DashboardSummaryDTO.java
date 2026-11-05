package lk.ijse.vishmibakehouse.dto;

import java.math.BigDecimal;

public class DashboardSummaryDTO {
    private BigDecimal todaySales;
    private int unitsProducedToday;
    private int pendingDeliveries;
    private int lowStockIngredients;

    public DashboardSummaryDTO() {
        this.todaySales = BigDecimal.ZERO;
    }

    public DashboardSummaryDTO(BigDecimal todaySales, int unitsProducedToday, int pendingDeliveries, int lowStockIngredients) {
        this.todaySales = todaySales;
        this.unitsProducedToday = unitsProducedToday;
        this.pendingDeliveries = pendingDeliveries;
        this.lowStockIngredients = lowStockIngredients;
    }

    public BigDecimal getTodaySales() { return todaySales; }
    public void setTodaySales(BigDecimal todaySales) { this.todaySales = todaySales; }

    public int getUnitsProducedToday() { return unitsProducedToday; }
    public void setUnitsProducedToday(int unitsProducedToday) { this.unitsProducedToday = unitsProducedToday; }

    public int getPendingDeliveries() { return pendingDeliveries; }
    public void setPendingDeliveries(int pendingDeliveries) { this.pendingDeliveries = pendingDeliveries; }

    public int getLowStockIngredients() { return lowStockIngredients; }
    public void setLowStockIngredients(int lowStockIngredients) { this.lowStockIngredients = lowStockIngredients; }
}
