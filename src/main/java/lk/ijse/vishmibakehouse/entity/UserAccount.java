package lk.ijse.vishmibakehouse.entity;

import java.sql.Timestamp;

public class UserAccount {
    private long userAccountId;
    private long employeeId;
    private String username;
    private String passwordHash;
    private String accessRole;
    private boolean active;
    private Timestamp createdAt;

    public UserAccount() {}

    public UserAccount(long userAccountId, long employeeId, String username, String passwordHash, String accessRole, boolean active) {
        this.userAccountId = userAccountId;
        this.employeeId = employeeId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.accessRole = accessRole;
        this.active = active;
    }

    public long getUserAccountId() { return userAccountId; }
    public void setUserAccountId(long userAccountId) { this.userAccountId = userAccountId; }

    public long getEmployeeId() { return employeeId; }
    public void setEmployeeId(long employeeId) { this.employeeId = employeeId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getAccessRole() { return accessRole; }
    public void setAccessRole(String accessRole) { this.accessRole = accessRole; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
