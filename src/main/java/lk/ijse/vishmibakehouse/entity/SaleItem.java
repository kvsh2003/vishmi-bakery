package lk.ijse.vishmibakehouse.entity;

import java.math.BigDecimal;

public class SaleItem {
    private long saleItemId;
    private long saleId;
    private long productId;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;

    public SaleItem() {}

    public SaleItem(long saleItemId, long saleId, long productId, BigDecimal quantity, BigDecimal unitPrice) {
        this.saleItemId = saleItemId;
        this.saleId = saleId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        if (quantity != null && unitPrice != null) {
            this.lineTotal = quantity.multiply(unitPrice);
        }
    }

    public long getSaleItemId() { return saleItemId; }
    public void setSaleItemId(long saleItemId) { this.saleItemId = saleItemId; }

    public long getSaleId() { return saleId; }
    public void setSaleId(long saleId) { this.saleId = saleId; }

    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }
}
