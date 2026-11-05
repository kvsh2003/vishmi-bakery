package lk.ijse.vishmibakehouse.dto;

public class CustomerDTO {
    private long customerId;
    private String name;
    private String phone;
    private String address;

    public CustomerDTO() {}

    public CustomerDTO(long customerId, String name, String phone, String address) {
        this.customerId = customerId;
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    public long getCustomerId() { return customerId; }
    public void setCustomerId(long customerId) { this.customerId = customerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() {
        return name + " (" + (phone != null ? phone : "No Phone") + ")";
    }
}
