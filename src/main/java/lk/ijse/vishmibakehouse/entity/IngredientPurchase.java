package lk.ijse.vishmibakehouse.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class IngredientPurchase {
    private long ingredientPurchaseId;
    private long ingredientId;
    private long recordedByEmployeeId;
    private Timestamp purchasedAt;
    private BigDecimal qty;
    private BigDecimal unitCost;
    private String supplierName;
    private BigDecimal totalCost;
    private Timestamp createdAt;

    public IngredientPurchase() {}

    public IngredientPurchase(long ingredientPurchaseId, long ingredientId, long recordedByEmployeeId, Timestamp purchasedAt, BigDecimal qty, BigDecimal unitCost, String supplierName) {
        this.ingredientPurchaseId = ingredientPurchaseId;
        this.ingredientId = ingredientId;
        this.recordedByEmployeeId = recordedByEmployeeId;
        this.purchasedAt = purchasedAt;
        this.qty = qty;
        this.unitCost = unitCost;
        this.supplierName = supplierName;
        if (qty != null && unitCost != null) {
            this.totalCost = qty.multiply(unitCost);
        }
    }

    public long getIngredientPurchaseId() { return ingredientPurchaseId; }
    public void setIngredientPurchaseId(long ingredientPurchaseId) { this.ingredientPurchaseId = ingredientPurchaseId; }

    public long getIngredientId() { return ingredientId; }
    public void setIngredientId(long ingredientId) { this.ingredientId = ingredientId; }

    public long getRecordedByEmployeeId() { return recordedByEmployeeId; }
    public void setRecordedByEmployeeId(long recordedByEmployeeId) { this.recordedByEmployeeId = recordedByEmployeeId; }

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

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
