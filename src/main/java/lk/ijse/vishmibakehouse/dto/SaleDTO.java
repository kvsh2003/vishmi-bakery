package lk.ijse.vishmibakehouse.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class SaleDTO {
    private long saleId;
    private long cashierEmployeeId;
    private String cashierName;
    private Timestamp soldAt;
    private BigDecimal cashReceived;
    private BigDecimal totalAmount;
    private BigDecimal changeAmount;
    private List<SaleItemDTO> items = new ArrayList<>();

    public SaleDTO() {}

    public SaleDTO(long saleId, long cashierEmployeeId, String cashierName, Timestamp soldAt, BigDecimal cashReceived, BigDecimal totalAmount, BigDecimal changeAmount) {
        this.saleId = saleId;
        this.cashierEmployeeId = cashierEmployeeId;
        this.cashierName = cashierName;
        this.soldAt = soldAt;
        this.cashReceived = cashReceived;
        this.totalAmount = totalAmount;
        this.changeAmount = changeAmount;
    }

    public long getSaleId() { return saleId; }
    public void setSaleId(long saleId) { this.saleId = saleId; }

    public long getCashierEmployeeId() { return cashierEmployeeId; }
    public void setCashierEmployeeId(long cashierEmployeeId) { this.cashierEmployeeId = cashierEmployeeId; }

    public String getCashierName() { return cashierName; }
    public void setCashierName(String cashierName) { this.cashierName = cashierName; }

    public Timestamp getSoldAt() { return soldAt; }
    public void setSoldAt(Timestamp soldAt) { this.soldAt = soldAt; }

    public BigDecimal getCashReceived() { return cashReceived; }
    public void setCashReceived(BigDecimal cashReceived) { this.cashReceived = cashReceived; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getChangeAmount() { return changeAmount; }
    public void setChangeAmount(BigDecimal changeAmount) { this.changeAmount = changeAmount; }

    public List<SaleItemDTO> getItems() { return items; }
    public void setItems(List<SaleItemDTO> items) { this.items = items; }
}
