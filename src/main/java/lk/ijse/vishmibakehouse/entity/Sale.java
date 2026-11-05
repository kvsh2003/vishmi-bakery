package lk.ijse.vishmibakehouse.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Sale {
    private long saleId;
    private long cashierEmployeeId;
    private Timestamp soldAt;
    private BigDecimal cashReceived;
    private Timestamp createdAt;

    public Sale() {}

    public Sale(long saleId, long cashierEmployeeId, Timestamp soldAt, BigDecimal cashReceived) {
        this.saleId = saleId;
        this.cashierEmployeeId = cashierEmployeeId;
        this.soldAt = soldAt;
        this.cashReceived = cashReceived;
    }

    public long getSaleId() { return saleId; }
    public void setSaleId(long saleId) { this.saleId = saleId; }

    public long getCashierEmployeeId() { return cashierEmployeeId; }
    public void setCashierEmployeeId(long cashierEmployeeId) { this.cashierEmployeeId = cashierEmployeeId; }

    public Timestamp getSoldAt() { return soldAt; }
    public void setSoldAt(Timestamp soldAt) { this.soldAt = soldAt; }

    public BigDecimal getCashReceived() { return cashReceived; }
    public void setCashReceived(BigDecimal cashReceived) { this.cashReceived = cashReceived; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
