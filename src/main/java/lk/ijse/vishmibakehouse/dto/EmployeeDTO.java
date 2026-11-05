package lk.ijse.vishmibakehouse.dto;

public class EmployeeDTO {
    private long employeeId;
    private String name;
    private String phone;
    private String address;
    private String jobRole;
    private boolean active;

    public EmployeeDTO() {}

    public EmployeeDTO(long employeeId, String name, String phone, String address, String jobRole, boolean active) {
        this.employeeId = employeeId;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.jobRole = jobRole;
        this.active = active;
    }

    public long getEmployeeId() { return employeeId; }
    public void setEmployeeId(long employeeId) { this.employeeId = employeeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getJobRole() { return jobRole; }
    public void setJobRole(String jobRole) { this.jobRole = jobRole; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
