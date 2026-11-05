package lk.ijse.vishmibakehouse.dto;

public class UserAccountDTO {
    private long userAccountId;
    private long employeeId;
    private String employeeName;
    private String username;
    private String password;
    private String accessRole;
    private boolean active;

    public UserAccountDTO() {}

    public UserAccountDTO(long userAccountId, long employeeId, String username, String accessRole, boolean active) {
        this.userAccountId = userAccountId;
        this.employeeId = employeeId;
        this.username = username;
        this.accessRole = accessRole;
        this.active = active;
    }

    public long getUserAccountId() { return userAccountId; }
    public void setUserAccountId(long userAccountId) { this.userAccountId = userAccountId; }

    public long getEmployeeId() { return employeeId; }
    public void setEmployeeId(long employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getAccessRole() { return accessRole; }
    public void setAccessRole(String accessRole) { this.accessRole = accessRole; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
