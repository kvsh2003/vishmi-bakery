package lk.ijse.vishmibakehouse.dto;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class DeliveryDTO {
    private long deliveryId;
    private long shopId;
    private String shopName;
    private Long vehicleId;
    private String vehicleRegistration;
    private Long deliveryEmployeeId;
    private String deliveryEmployeeName;
    private long packedByEmployeeId;
    private String packedByEmployeeName;
    private Date deliveryDate;
    private String status;
    private List<DeliveryItemDTO> items = new ArrayList<>();

    public DeliveryDTO() {}

    public DeliveryDTO(long deliveryId, long shopId, String shopName, Long vehicleId, String vehicleRegistration, Long deliveryEmployeeId, String deliveryEmployeeName, long packedByEmployeeId, String packedByEmployeeName, Date deliveryDate, String status) {
        this.deliveryId = deliveryId;
        this.shopId = shopId;
        this.shopName = shopName;
        this.vehicleId = vehicleId;
        this.vehicleRegistration = vehicleRegistration;
        this.deliveryEmployeeId = deliveryEmployeeId;
        this.deliveryEmployeeName = deliveryEmployeeName;
        this.packedByEmployeeId = packedByEmployeeId;
        this.packedByEmployeeName = packedByEmployeeName;
        this.deliveryDate = deliveryDate;
        this.status = status;
    }

    public long getDeliveryId() { return deliveryId; }
    public void setDeliveryId(long deliveryId) { this.deliveryId = deliveryId; }

    public long getShopId() { return shopId; }
    public void setShopId(long shopId) { this.shopId = shopId; }

    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

    public String getVehicleRegistration() { return vehicleRegistration; }
    public void setVehicleRegistration(String vehicleRegistration) { this.vehicleRegistration = vehicleRegistration; }

    public Long getDeliveryEmployeeId() { return deliveryEmployeeId; }
    public void setDeliveryEmployeeId(Long deliveryEmployeeId) { this.deliveryEmployeeId = deliveryEmployeeId; }

    public String getDeliveryEmployeeName() { return deliveryEmployeeName; }
    public void setDeliveryEmployeeName(String deliveryEmployeeName) { this.deliveryEmployeeName = deliveryEmployeeName; }

    public long getPackedByEmployeeId() { return packedByEmployeeId; }
    public void setPackedByEmployeeId(long packedByEmployeeId) { this.packedByEmployeeId = packedByEmployeeId; }

    public String getPackedByEmployeeName() { return packedByEmployeeName; }
    public void setPackedByEmployeeName(String packedByEmployeeName) { this.packedByEmployeeName = packedByEmployeeName; }

    public Date getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(Date deliveryDate) { this.deliveryDate = deliveryDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<DeliveryItemDTO> getItems() { return items; }
    public void setItems(List<DeliveryItemDTO> items) { this.items = items; }
}
