package lk.ijse.vishmibakehouse.entity;

import java.sql.Timestamp;

public class Vehicle {
    private long vehicleId;
    private String registrationNo;
    private String model;
    private boolean active;
    private Timestamp createdAt;

    public Vehicle() {}

    public Vehicle(long vehicleId, String registrationNo, String model, boolean active) {
        this.vehicleId = vehicleId;
        this.registrationNo = registrationNo;
        this.model = model;
        this.active = active;
    }

    public long getVehicleId() { return vehicleId; }
    public void setVehicleId(long vehicleId) { this.vehicleId = vehicleId; }

    public String getRegistrationNo() { return registrationNo; }
    public void setRegistrationNo(String registrationNo) { this.registrationNo = registrationNo; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
