package lk.ijse.vishmibakehouse.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class SpecialOrder {
    private long specialOrderId;
    private long customerId;
    private long recordedByEmployeeId;
    private String orderType; // WEDDING_CAKE, SPECIAL
    private Timestamp requiredAt;
    private String status; // PENDING, CONFIRMED, IN_PROGRESS, READY, COMPLETED, CANCELLED
    private String instructions;
    private BigDecimal depositAmount;
    private Timestamp createdAt;

    public SpecialOrder() {}

    public SpecialOrder(long specialOrderId, long customerId, long recordedByEmployeeId, String orderType, Timestamp requiredAt, String status, String instructions, BigDecimal depositAmount) {
        this.specialOrderId = specialOrderId;
        this.customerId = customerId;
        this.recordedByEmployeeId = recordedByEmployeeId;
        this.orderType = orderType;
        this.requiredAt = requiredAt;
        this.status = status;
        this.instructions = instructions;
        this.depositAmount = depositAmount;
    }

    public long getSpecialOrderId() { return specialOrderId; }
    public void setSpecialOrderId(long specialOrderId) { this.specialOrderId = specialOrderId; }

    public long getCustomerId() { return customerId; }
    public void setCustomerId(long customerId) { this.customerId = customerId; }

    public long getRecordedByEmployeeId() { return recordedByEmployeeId; }
    public void setRecordedByEmployeeId(long recordedByEmployeeId) { this.recordedByEmployeeId = recordedByEmployeeId; }

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

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
