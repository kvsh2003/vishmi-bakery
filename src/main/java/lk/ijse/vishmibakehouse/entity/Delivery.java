package lk.ijse.vishmibakehouse.entity;

import java.sql.Date;
import java.sql.Timestamp;

public class Delivery {
    private long deliveryId;
    private long shopId;
    private Long vehicleId; // nullable until assigned
    private Long deliveryEmployeeId; // nullable until assigned
    private long packedByEmployeeId;
    private Date deliveryDate;
    private String status; // PACKED, DISPATCHED, DELIVERED, CANCELLED
    private Timestamp createdAt;

    public Delivery() {}

    public Delivery(long deliveryId, long shopId, Long vehicleId, Long deliveryEmployeeId, long packedByEmployeeId, Date deliveryDate, String status) {
        this.deliveryId = deliveryId;
        this.shopId = shopId;
        this.vehicleId = vehicleId;
        this.deliveryEmployeeId = deliveryEmployeeId;
        this.packedByEmployeeId = packedByEmployeeId;
        this.deliveryDate = deliveryDate;
        this.status = status;
    }

    public long getDeliveryId() { return deliveryId; }
    public void setDeliveryId(long deliveryId) { this.deliveryId = deliveryId; }

    public long getShopId() { return shopId; }
    public void setShopId(long shopId) { this.shopId = shopId; }

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

    public Long getDeliveryEmployeeId() { return deliveryEmployeeId; }
    public void setDeliveryEmployeeId(Long deliveryEmployeeId) { this.deliveryEmployeeId = deliveryEmployeeId; }

    public long getPackedByEmployeeId() { return packedByEmployeeId; }
    public void setPackedByEmployeeId(long packedByEmployeeId) { this.packedByEmployeeId = packedByEmployeeId; }

    public Date getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(Date deliveryDate) { this.deliveryDate = deliveryDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
