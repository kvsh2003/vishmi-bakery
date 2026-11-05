package lk.ijse.vishmibakehouse.entity;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class ProductionBatch {
    private long productionBatchId;
    private long productId;
    private long recordedByEmployeeId;
    private Date producedOn;
    private BigDecimal plannedQty;
    private BigDecimal actualQty;
    private String status; // PLANNED, IN_PROGRESS, COMPLETED, CANCELLED
    private Timestamp createdAt;

    public ProductionBatch() {}

    public ProductionBatch(long productionBatchId, long productId, long recordedByEmployeeId, Date producedOn, BigDecimal plannedQty, BigDecimal actualQty, String status) {
        this.productionBatchId = productionBatchId;
        this.productId = productId;
        this.recordedByEmployeeId = recordedByEmployeeId;
        this.producedOn = producedOn;
        this.plannedQty = plannedQty;
        this.actualQty = actualQty;
        this.status = status;
    }

    public long getProductionBatchId() { return productionBatchId; }
    public void setProductionBatchId(long productionBatchId) { this.productionBatchId = productionBatchId; }

    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }

    public long getRecordedByEmployeeId() { return recordedByEmployeeId; }
    public void setRecordedByEmployeeId(long recordedByEmployeeId) { this.recordedByEmployeeId = recordedByEmployeeId; }

    public Date getProducedOn() { return producedOn; }
    public void setProducedOn(Date producedOn) { this.producedOn = producedOn; }

    public BigDecimal getPlannedQty() { return plannedQty; }
    public void setPlannedQty(BigDecimal plannedQty) { this.plannedQty = plannedQty; }

    public BigDecimal getActualQty() { return actualQty; }
    public void setActualQty(BigDecimal actualQty) { this.actualQty = actualQty; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
