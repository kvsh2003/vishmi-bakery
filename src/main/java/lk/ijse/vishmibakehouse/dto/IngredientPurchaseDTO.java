package lk.ijse.vishmibakehouse.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class IngredientPurchaseDTO {
    private long ingredientPurchaseId;
    private long ingredientId;
    private String ingredientName;
    private long recordedByEmployeeId;
    private String employeeName;
    private Timestamp purchasedAt;
    private BigDecimal qty;
    private BigDecimal unitCost;
    private String supplierName;
    private BigDecimal totalCost;

    public IngredientPurchaseDTO() {}

    public IngredientPurchaseDTO(long ingredientPurchaseId, long ingredientId, String ingredientName, long recordedByEmployeeId, String employeeName, Timestamp purchasedAt, BigDecimal qty, BigDecimal unitCost, String supplierName, BigDecimal totalCost) {
        this.ingredientPurchaseId = ingredientPurchaseId;
        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
        this.recordedByEmployeeId = recordedByEmployeeId;
        this.employeeName = employeeName;
        this.purchasedAt = purchasedAt;
        this.qty = qty;
        this.unitCost = unitCost;
        this.supplierName = supplierName;
        this.totalCost = totalCost;
    }

    public long getIngredientPurchaseId() { return ingredientPurchaseId; }
    public void setIngredientPurchaseId(long ingredientPurchaseId) { this.ingredientPurchaseId = ingredientPurchaseId; }

    public long getIngredientId() { return ingredientId; }
    public void setIngredientId(long ingredientId) { this.ingredientId = ingredientId; }

    public String getIngredientName() { return ingredientName; }
    public void setIngredientName(String ingredientName) { this.ingredientName = ingredientName; }

    public long getRecordedByEmployeeId() { return recordedByEmployeeId; }
    public void setRecordedByEmployeeId(long recordedByEmployeeId) { this.recordedByEmployeeId = recordedByEmployeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public Timestamp getPurchasedAt() { return purchasedAt; }
    public void setPurchasedAt(Timestamp purchasedAt) { this.purchasedAt = purchasedAt; }

    public BigDecimal getQty() { return qty; }
    public void setQty(BigDecimal qty) { this.qty = qty; }

    public BigDecimal getUnitCost() { return unitCost; }
    public void setUnitCost(BigDecimal unitCost) { this.unitCost = unitCost; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
}
