package lk.ijse.vishmibakehouse.entity;

import java.sql.Timestamp;

public class Shop {
    private long shopId;
    private String name;
    private String phone;
    private String address;
    private boolean active;
    private Timestamp createdAt;

    public Shop() {}

    public Shop(long shopId, String name, String phone, String address, boolean active) {
        this.shopId = shopId;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.active = active;
    }

    public long getShopId() { return shopId; }
    public void setShopId(long shopId) { this.shopId = shopId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
