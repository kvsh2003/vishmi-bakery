package lk.ijse.vishmibakehouse.dto;

public class ShopDTO {
    private long shopId;
    private String name;
    private String phone;
    private String address;
    private boolean active;

    public ShopDTO() {}

    public ShopDTO(long shopId, String name, String phone, String address, boolean active) {
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

    @Override
    public String toString() {
        return name;
    }
}
