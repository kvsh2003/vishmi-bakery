package lk.ijse.vishmibakehouse.dto;

import java.math.BigDecimal;
import java.sql.Date;

public class ProductionBatchDTO {
    private long productionBatchId;
    private long productId;
    private String productName;
    private long recordedByEmployeeId;
    private String employeeName;
    private Date producedOn;
    private BigDecimal plannedQty;
    private BigDecimal actualQty;
    private String status;

    public ProductionBatchDTO() {}

    public ProductionBatchDTO(long productionBatchId, long productId, String productName, long recordedByEmployeeId, String employeeName, Date producedOn, BigDecimal plannedQty, BigDecimal actualQty, String status) {
        this.productionBatchId = productionBatchId;
        this.productId = productId;
        this.productName = productName;
        this.recordedByEmployeeId = recordedByEmployeeId;
        this.employeeName = employeeName;
        this.producedOn = producedOn;
        this.plannedQty = plannedQty;
        this.actualQty = actualQty;
        this.status = status;
    }

    public long getProductionBatchId() { return productionBatchId; }
    public void setProductionBatchId(long productionBatchId) { this.productionBatchId = productionBatchId; }

    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public long getRecordedByEmployeeId() { return recordedByEmployeeId; }
    public void setRecordedByEmployeeId(long recordedByEmployeeId) { this.recordedByEmployeeId = recordedByEmployeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public Date getProducedOn() { return producedOn; }
    public void setProducedOn(Date producedOn) { this.producedOn = producedOn; }

    public BigDecimal getPlannedQty() { return plannedQty; }
    public void setPlannedQty(BigDecimal plannedQty) { this.plannedQty = plannedQty; }

    public BigDecimal getActualQty() { return actualQty; }
    public void setActualQty(BigDecimal actualQty) { this.actualQty = actualQty; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "#" + productionBatchId + " - " + productName + " (" + producedOn + ")";
    }
}
