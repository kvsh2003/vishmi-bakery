package lk.ijse.vishmibakehouse.dto;

import java.math.BigDecimal;
import java.sql.Date;

public class DeliverySummaryDTO {
    private long deliveryId;
    private String shopName;
    private String registrationNo;
    private String deliveryStaff;
    private Date deliveryDate;
    private String status;
    private BigDecimal packedQty;
    private BigDecimal deliveredQty;

    public DeliverySummaryDTO() {}

    public DeliverySummaryDTO(long deliveryId, String shopName, String registrationNo, String deliveryStaff, Date deliveryDate, String status, BigDecimal packedQty, BigDecimal deliveredQty) {
        this.deliveryId = deliveryId;
        this.shopName = shopName;
        this.registrationNo = registrationNo;
        this.deliveryStaff = deliveryStaff;
        this.deliveryDate = deliveryDate;
        this.status = status;
        this.packedQty = packedQty;
        this.deliveredQty = deliveredQty;
    }

    public long getDeliveryId() { return deliveryId; }
    public void setDeliveryId(long deliveryId) { this.deliveryId = deliveryId; }

    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }

    public String getRegistrationNo() { return registrationNo; }
    public void setRegistrationNo(String registrationNo) { this.registrationNo = registrationNo; }

    public String getDeliveryStaff() { return deliveryStaff; }
    public void setDeliveryStaff(String deliveryStaff) { this.deliveryStaff = deliveryStaff; }

    public Date getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(Date deliveryDate) { this.deliveryDate = deliveryDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getPackedQty() { return packedQty; }
    public void setPackedQty(BigDecimal packedQty) { this.packedQty = packedQty; }

    public BigDecimal getDeliveredQty() { return deliveredQty; }
    public void setDeliveredQty(BigDecimal deliveredQty) { this.deliveredQty = deliveredQty; }
}
