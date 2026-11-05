package lk.ijse.vishmibakehouse.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BreadCrumbBatchDTO {
    private long breadCrumbBatchId;
    private long outputProductId;
    private String outputProductName;
    private long recordedByEmployeeId;
    private String employeeName;
    private Timestamp producedAt;
    private BigDecimal outputQtyKg;
    private List<BreadCrumbInputDTO> inputs = new ArrayList<>();

    public BreadCrumbBatchDTO() {}

    public BreadCrumbBatchDTO(long breadCrumbBatchId, long outputProductId, String outputProductName, long recordedByEmployeeId, String employeeName, Timestamp producedAt, BigDecimal outputQtyKg) {
        this.breadCrumbBatchId = breadCrumbBatchId;
        this.outputProductId = outputProductId;
        this.outputProductName = outputProductName;
        this.recordedByEmployeeId = recordedByEmployeeId;
        this.employeeName = employeeName;
        this.producedAt = producedAt;
        this.outputQtyKg = outputQtyKg;
    }

    public long getBreadCrumbBatchId() { return breadCrumbBatchId; }
    public void setBreadCrumbBatchId(long breadCrumbBatchId) { this.breadCrumbBatchId = breadCrumbBatchId; }

    public long getOutputProductId() { return outputProductId; }
    public void setOutputProductId(long outputProductId) { this.outputProductId = outputProductId; }

    public String getOutputProductName() { return outputProductName; }
    public void setOutputProductName(String outputProductName) { this.outputProductName = outputProductName; }

    public long getRecordedByEmployeeId() { return recordedByEmployeeId; }
    public void setRecordedByEmployeeId(long recordedByEmployeeId) { this.recordedByEmployeeId = recordedByEmployeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public Timestamp getProducedAt() { return producedAt; }
    public void setProducedAt(Timestamp producedAt) { this.producedAt = producedAt; }

    public BigDecimal getOutputQtyKg() { return outputQtyKg; }
    public void setOutputQtyKg(BigDecimal outputQtyKg) { this.outputQtyKg = outputQtyKg; }

    public List<BreadCrumbInputDTO> getInputs() { return inputs; }
    public void setInputs(List<BreadCrumbInputDTO> inputs) { this.inputs = inputs; }
}
