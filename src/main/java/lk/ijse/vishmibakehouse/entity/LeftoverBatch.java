package lk.ijse.vishmibakehouse.entity;

import java.sql.Date;
import java.sql.Timestamp;

public class LeftoverBatch {
    private long leftoverBatchId;
    private long productionBatchId;
    private long recordedByEmployeeId;
    private Date recordedOn;
    private int qtyPieces;
    private Timestamp createdAt;

    public LeftoverBatch() {}

    public LeftoverBatch(long leftoverBatchId, long productionBatchId, long recordedByEmployeeId, Date recordedOn, int qtyPieces) {
        this.leftoverBatchId = leftoverBatchId;
        this.productionBatchId = productionBatchId;
        this.recordedByEmployeeId = recordedByEmployeeId;
        this.recordedOn = recordedOn;
        this.qtyPieces = qtyPieces;
    }

    public long getLeftoverBatchId() { return leftoverBatchId; }
    public void setLeftoverBatchId(long leftoverBatchId) { this.leftoverBatchId = leftoverBatchId; }

    public long getProductionBatchId() { return productionBatchId; }
    public void setProductionBatchId(long productionBatchId) { this.productionBatchId = productionBatchId; }

    public long getRecordedByEmployeeId() { return recordedByEmployeeId; }
    public void setRecordedByEmployeeId(long recordedByEmployeeId) { this.recordedByEmployeeId = recordedByEmployeeId; }

    public Date getRecordedOn() { return recordedOn; }
    public void setRecordedOn(Date recordedOn) { this.recordedOn = recordedOn; }

    public int getQtyPieces() { return qtyPieces; }
    public void setQtyPieces(int qtyPieces) { this.qtyPieces = qtyPieces; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
