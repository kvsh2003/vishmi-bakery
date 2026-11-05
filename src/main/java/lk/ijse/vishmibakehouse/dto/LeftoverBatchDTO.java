package lk.ijse.vishmibakehouse.dto;

import java.sql.Date;

public class LeftoverBatchDTO {
    private long leftoverBatchId;
    private long productionBatchId;
    private String productName;
    private long recordedByEmployeeId;
    private String employeeName;
    private Date recordedOn;
    private int qtyPieces;

    public LeftoverBatchDTO() {}

    public LeftoverBatchDTO(long leftoverBatchId, long productionBatchId, String productName, long recordedByEmployeeId, String employeeName, Date recordedOn, int qtyPieces) {
        this.leftoverBatchId = leftoverBatchId;
        this.productionBatchId = productionBatchId;
        this.productName = productName;
        this.recordedByEmployeeId = recordedByEmployeeId;
        this.employeeName = employeeName;
        this.recordedOn = recordedOn;
        this.qtyPieces = qtyPieces;
    }

    public long getLeftoverBatchId() { return leftoverBatchId; }
    public void setLeftoverBatchId(long leftoverBatchId) { this.leftoverBatchId = leftoverBatchId; }

    public long getProductionBatchId() { return productionBatchId; }
    public void setProductionBatchId(long productionBatchId) { this.productionBatchId = productionBatchId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public long getRecordedByEmployeeId() { return recordedByEmployeeId; }
    public void setRecordedByEmployeeId(long recordedByEmployeeId) { this.recordedByEmployeeId = recordedByEmployeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public Date getRecordedOn() { return recordedOn; }
    public void setRecordedOn(Date recordedOn) { this.recordedOn = recordedOn; }

    public int getQtyPieces() { return qtyPieces; }
    public void setQtyPieces(int qtyPieces) { this.qtyPieces = qtyPieces; }

    @Override
    public String toString() {
        return "Leftover Lot #" + leftoverBatchId + " (" + qtyPieces + " pcs) - " + productName;
    }
}
