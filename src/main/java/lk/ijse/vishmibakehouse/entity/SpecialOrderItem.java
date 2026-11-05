package lk.ijse.vishmibakehouse.entity;

import java.math.BigDecimal;

public class SpecialOrderItem {
    private long specialOrderItemId;
    private long specialOrderId;
    private Long productId; // nullable
    private String description;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;

    public SpecialOrderItem() {}

    public SpecialOrderItem(long specialOrderItemId, long specialOrderId, Long productId, String description, BigDecimal quantity, BigDecimal unitPrice) {
        this.specialOrderItemId = specialOrderItemId;
        this.specialOrderId = specialOrderId;
        this.productId = productId;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        if (quantity != null && unitPrice != null) {
            this.lineTotal = quantity.multiply(unitPrice);
        }
    }

    public long getSpecialOrderItemId() { return specialOrderItemId; }
    public void setSpecialOrderItemId(long specialOrderItemId) { this.specialOrderItemId = specialOrderItemId; }

    public long getSpecialOrderId() { return specialOrderId; }
    public void setSpecialOrderId(long specialOrderId) { this.specialOrderId = specialOrderId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }
}
