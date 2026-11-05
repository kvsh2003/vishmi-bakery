package lk.ijse.vishmibakehouse.entity;

import java.math.BigDecimal;

public class DeliveryItem {
    private long deliveryItemId;
    private long deliveryId;
    private long productId;
    private BigDecimal packedQty;
    private BigDecimal deliveredQty;

    public DeliveryItem() {}

    public DeliveryItem(long deliveryItemId, long deliveryId, long productId, BigDecimal packedQty, BigDecimal deliveredQty) {
        this.deliveryItemId = deliveryItemId;
        this.deliveryId = deliveryId;
        this.productId = productId;
        this.packedQty = packedQty;
        this.deliveredQty = deliveredQty;
    }

    public long getDeliveryItemId() { return deliveryItemId; }
    public void setDeliveryItemId(long deliveryItemId) { this.deliveryItemId = deliveryItemId; }

    public long getDeliveryId() { return deliveryId; }
    public void setDeliveryId(long deliveryId) { this.deliveryId = deliveryId; }

    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }

    public BigDecimal getPackedQty() { return packedQty; }
    public void setPackedQty(BigDecimal packedQty) { this.packedQty = packedQty; }

    public BigDecimal getDeliveredQty() { return deliveredQty; }
    public void setDeliveredQty(BigDecimal deliveredQty) { this.deliveredQty = deliveredQty; }
}
