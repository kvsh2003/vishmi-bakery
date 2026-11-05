package lk.ijse.vishmibakehouse.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class IngredientUsage {
    private long ingredientUsageId;
    private long productionBatchId;
    private long ingredientId;
    private long recordedByEmployeeId;
    private Timestamp usedAt;
    private BigDecimal qty;
    private Timestamp createdAt;

    public IngredientUsage() {}

    public IngredientUsage(long ingredientUsageId, long productionBatchId, long ingredientId, long recordedByEmployeeId, Timestamp usedAt, BigDecimal qty) {
        this.ingredientUsageId = ingredientUsageId;
        this.productionBatchId = productionBatchId;
        this.ingredientId = ingredientId;
        this.recordedByEmployeeId = recordedByEmployeeId;
        this.usedAt = usedAt;
        this.qty = qty;
    }

    public long getIngredientUsageId() { return ingredientUsageId; }
    public void setIngredientUsageId(long ingredientUsageId) { this.ingredientUsageId = ingredientUsageId; }

    public long getProductionBatchId() { return productionBatchId; }
    public void setProductionBatchId(long productionBatchId) { this.productionBatchId = productionBatchId; }

    public long getIngredientId() { return ingredientId; }
    public void setIngredientId(long ingredientId) { this.ingredientId = ingredientId; }

    public long getRecordedByEmployeeId() { return recordedByEmployeeId; }
    public void setRecordedByEmployeeId(long recordedByEmployeeId) { this.recordedByEmployeeId = recordedByEmployeeId; }

    public Timestamp getUsedAt() { return usedAt; }
    public void setUsedAt(Timestamp usedAt) { this.usedAt = usedAt; }

    public BigDecimal getQty() { return qty; }
    public void setQty(BigDecimal qty) { this.qty = qty; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
