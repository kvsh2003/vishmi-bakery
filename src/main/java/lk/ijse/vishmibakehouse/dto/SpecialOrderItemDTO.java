package lk.ijse.vishmibakehouse.dto;

import java.math.BigDecimal;

public class SpecialOrderItemDTO {
    private long specialOrderItemId;
    private long specialOrderId;
    private Long productId;
    private String productName;
    private String description;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;

    public SpecialOrderItemDTO() {}

    public SpecialOrderItemDTO(long specialOrderItemId, long specialOrderId, Long productId, String productName, String description, BigDecimal quantity, BigDecimal unitPrice, BigDecimal lineTotal) {
        this.specialOrderItemId = specialOrderItemId;
        this.specialOrderId = specialOrderId;
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.lineTotal = lineTotal;
    }

    public long getSpecialOrderItemId() { return specialOrderItemId; }
    public void setSpecialOrderItemId(long specialOrderItemId) { this.specialOrderItemId = specialOrderItemId; }

    public long getSpecialOrderId() { return specialOrderId; }
    public void setSpecialOrderId(long specialOrderId) { this.specialOrderId = specialOrderId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }
}
