package lk.ijse.vishmibakehouse.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class IngredientUsageDTO {
    private long ingredientUsageId;
    private long productionBatchId;
    private String productName;
    private long ingredientId;
    private String ingredientName;
    private long recordedByEmployeeId;
    private String employeeName;
    private Timestamp usedAt;
    private BigDecimal qty;

    public IngredientUsageDTO() {}

    public IngredientUsageDTO(long ingredientUsageId, long productionBatchId, String productName, long ingredientId, String ingredientName, long recordedByEmployeeId, String employeeName, Timestamp usedAt, BigDecimal qty) {
        this.ingredientUsageId = ingredientUsageId;
        this.productionBatchId = productionBatchId;
        this.productName = productName;
        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
        this.recordedByEmployeeId = recordedByEmployeeId;
        this.employeeName = employeeName;
        this.usedAt = usedAt;
        this.qty = qty;
    }

    public long getIngredientUsageId() { return ingredientUsageId; }
    public void setIngredientUsageId(long ingredientUsageId) { this.ingredientUsageId = ingredientUsageId; }

    public long getProductionBatchId() { return productionBatchId; }
    public void setProductionBatchId(long productionBatchId) { this.productionBatchId = productionBatchId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public long getIngredientId() { return ingredientId; }
    public void setIngredientId(long ingredientId) { this.ingredientId = ingredientId; }

    public String getIngredientName() { return ingredientName; }
    public void setIngredientName(String ingredientName) { this.ingredientName = ingredientName; }

    public long getRecordedByEmployeeId() { return recordedByEmployeeId; }
    public void setRecordedByEmployeeId(long recordedByEmployeeId) { this.recordedByEmployeeId = recordedByEmployeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public Timestamp getUsedAt() { return usedAt; }
    public void setUsedAt(Timestamp usedAt) { this.usedAt = usedAt; }

    public BigDecimal getQty() { return qty; }
    public void setQty(BigDecimal qty) { this.qty = qty; }
}
