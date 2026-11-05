package lk.ijse.vishmibakehouse.dto;

import java.sql.Date;

public class LeftoverBalanceDTO {
    private long leftoverBatchId;
    private long productionBatchId;
    private Date recordedOn;
    private int qtyPieces;
    private int usedPieces;
    private int remainingPieces;

    public LeftoverBalanceDTO() {}

    public LeftoverBalanceDTO(long leftoverBatchId, long productionBatchId, Date recordedOn, int qtyPieces, int usedPieces, int remainingPieces) {
        this.leftoverBatchId = leftoverBatchId;
        this.productionBatchId = productionBatchId;
        this.recordedOn = recordedOn;
        this.qtyPieces = qtyPieces;
        this.usedPieces = usedPieces;
        this.remainingPieces = remainingPieces;
    }

    public long getLeftoverBatchId() { return leftoverBatchId; }
    public void setLeftoverBatchId(long leftoverBatchId) { this.leftoverBatchId = leftoverBatchId; }

    public long getProductionBatchId() { return productionBatchId; }
    public void setProductionBatchId(long productionBatchId) { this.productionBatchId = productionBatchId; }

    public Date getRecordedOn() { return recordedOn; }
    public void setRecordedOn(Date recordedOn) { this.recordedOn = recordedOn; }

    public int getQtyPieces() { return qtyPieces; }
    public void setQtyPieces(int qtyPieces) { this.qtyPieces = qtyPieces; }

    public int getUsedPieces() { return usedPieces; }
    public void setUsedPieces(int usedPieces) { this.usedPieces = usedPieces; }

    public int getRemainingPieces() { return remainingPieces; }
    public void setRemainingPieces(int remainingPieces) { this.remainingPieces = remainingPieces; }
}
