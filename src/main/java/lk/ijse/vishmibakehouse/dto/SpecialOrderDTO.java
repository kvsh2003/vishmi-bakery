package lk.ijse.vishmibakehouse.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class SpecialOrderDTO {
    private long specialOrderId;
    private long customerId;
    private String customerName;
    private String customerPhone;
    private long recordedByEmployeeId;
    private String employeeName;
    private String orderType;
    private Timestamp requiredAt;
    private String status;
    private String instructions;
    private BigDecimal depositAmount;
    private BigDecimal totalAmount;
    private BigDecimal balanceAmount;
    private List<SpecialOrderItemDTO> items = new ArrayList<>();

    public SpecialOrderDTO() {}

    public SpecialOrderDTO(long specialOrderId, long customerId, String customerName, String customerPhone, long recordedByEmployeeId, String employeeName, String orderType, Timestamp requiredAt, String status, String instructions, BigDecimal depositAmount, BigDecimal totalAmount, BigDecimal balanceAmount) {
        this.specialOrderId = specialOrderId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.recordedByEmployeeId = recordedByEmployeeId;
        this.employeeName = employeeName;
        this.orderType = orderType;
        this.requiredAt = requiredAt;
        this.status = status;
        this.instructions = instructions;
        this.depositAmount = depositAmount;
        this.totalAmount = totalAmount;
        this.balanceAmount = balanceAmount;
    }

    public long getSpecialOrderId() { return specialOrderId; }
    public void setSpecialOrderId(long specialOrderId) { this.specialOrderId = specialOrderId; }

    public long getCustomerId() { return customerId; }
    public void setCustomerId(long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public long getRecordedByEmployeeId() { return recordedByEmployeeId; }
    public void setRecordedByEmployeeId(long recordedByEmployeeId) { this.recordedByEmployeeId = recordedByEmployeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }

    public Timestamp getRequiredAt() { return requiredAt; }
    public void setRequiredAt(Timestamp requiredAt) { this.requiredAt = requiredAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public BigDecimal getDepositAmount() { return depositAmount; }
    public void setDepositAmount(BigDecimal depositAmount) { this.depositAmount = depositAmount; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getBalanceAmount() { return balanceAmount; }
    public void setBalanceAmount(BigDecimal balanceAmount) { this.balanceAmount = balanceAmount; }

    public List<SpecialOrderItemDTO> getItems() { return items; }
    public void setItems(List<SpecialOrderItemDTO> items) { this.items = items; }
}
