package lk.ijse.vishmibakehouse.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class BreadCrumbBatch {
    private long breadCrumbBatchId;
    private long outputProductId;
    private long recordedByEmployeeId;
    private Timestamp producedAt;
    private BigDecimal outputQtyKg;
    private Timestamp createdAt;

    public BreadCrumbBatch() {}

    public BreadCrumbBatch(long breadCrumbBatchId, long outputProductId, long recordedByEmployeeId, Timestamp producedAt, BigDecimal outputQtyKg) {
        this.breadCrumbBatchId = breadCrumbBatchId;
        this.outputProductId = outputProductId;
        this.recordedByEmployeeId = recordedByEmployeeId;
        this.producedAt = producedAt;
        this.outputQtyKg = outputQtyKg;
    }

    public long getBreadCrumbBatchId() { return breadCrumbBatchId; }
    public void setBreadCrumbBatchId(long breadCrumbBatchId) { this.breadCrumbBatchId = breadCrumbBatchId; }

    public long getOutputProductId() { return outputProductId; }
    public void setOutputProductId(long outputProductId) { this.outputProductId = outputProductId; }

    public long getRecordedByEmployeeId() { return recordedByEmployeeId; }
    public void setRecordedByEmployeeId(long recordedByEmployeeId) { this.recordedByEmployeeId = recordedByEmployeeId; }

    public Timestamp getProducedAt() { return producedAt; }
    public void setProducedAt(Timestamp producedAt) { this.producedAt = producedAt; }

    public BigDecimal getOutputQtyKg() { return outputQtyKg; }
    public void setOutputQtyKg(BigDecimal outputQtyKg) { this.outputQtyKg = outputQtyKg; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
