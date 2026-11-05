package lk.ijse.vishmibakehouse.dto;

public class VehicleDTO {
    private long vehicleId;
    private String registrationNo;
    private String model;
    private boolean active;

    public VehicleDTO() {}

    public VehicleDTO(long vehicleId, String registrationNo, String model, boolean active) {
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

    @Override
    public String toString() {
        return registrationNo + " - " + model;
    }
}
