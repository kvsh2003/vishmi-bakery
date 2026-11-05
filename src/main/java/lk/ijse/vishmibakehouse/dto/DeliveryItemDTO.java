package lk.ijse.vishmibakehouse.dto;

import java.math.BigDecimal;

public class DeliveryItemDTO {
    private long deliveryItemId;
    private long deliveryId;
    private long productId;
    private String productName;
    private BigDecimal packedQty;
    private BigDecimal deliveredQty;

    public DeliveryItemDTO() {}

    public DeliveryItemDTO(long deliveryItemId, long deliveryId, long productId, String productName, BigDecimal packedQty, BigDecimal deliveredQty) {
        this.deliveryItemId = deliveryItemId;
        this.deliveryId = deliveryId;
        this.productId = productId;
        this.productName = productName;
        this.packedQty = packedQty;
        this.deliveredQty = deliveredQty;
    }

    public long getDeliveryItemId() { return deliveryItemId; }
    public void setDeliveryItemId(long deliveryItemId) { this.deliveryItemId = deliveryItemId; }

    public long getDeliveryId() { return deliveryId; }
    public void setDeliveryId(long deliveryId) { this.deliveryId = deliveryId; }

    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public BigDecimal getPackedQty() { return packedQty; }
    public void setPackedQty(BigDecimal packedQty) { this.packedQty = packedQty; }

    public BigDecimal getDeliveredQty() { return deliveredQty; }
    public void setDeliveredQty(BigDecimal deliveredQty) { this.deliveredQty = deliveredQty; }
}
