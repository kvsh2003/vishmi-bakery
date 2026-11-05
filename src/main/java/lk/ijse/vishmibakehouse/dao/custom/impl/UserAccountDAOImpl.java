package lk.ijse.vishmibakehouse.dao.custom.impl;

import lk.ijse.vishmibakehouse.dao.SQLUtil;
import lk.ijse.vishmibakehouse.dao.custom.UserAccountDAO;
import lk.ijse.vishmibakehouse.entity.UserAccount;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserAccountDAOImpl implements UserAccountDAO {
    @Override
    public boolean save(UserAccount entity) throws SQLException {
        return SQLUtil.executeUpdate(
                "INSERT INTO user_account (employee_id, username, password_hash, access_role, active) VALUES (?, ?, ?, ?, ?)",
                entity.getEmployeeId(), entity.getUsername(), entity.getPasswordHash(), entity.getAccessRole(), entity.isActive() ? 1 : 0
        );
    }

    @Override
    public boolean update(UserAccount entity) throws SQLException {
        return SQLUtil.executeUpdate(
                "UPDATE user_account SET username=?, access_role=?, active=? WHERE user_account_id=?",
                entity.getUsername(), entity.getAccessRole(), entity.isActive() ? 1 : 0, entity.getUserAccountId()
        );
    }

    @Override
    public boolean delete(Long id) throws SQLException {
        return SQLUtil.executeUpdate("UPDATE user_account SET active=0 WHERE user_account_id=?", id);
    }

    @Override
    public Optional<UserAccount> findById(Long id) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM user_account WHERE user_account_id=?", id);
        if (rs.next()) {
            return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    @Override
    public List<UserAccount> findAll() throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM user_account ORDER BY user_account_id ASC");
        List<UserAccount> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public Optional<UserAccount> findByUsername(String username) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM user_account WHERE username=?", username);
        if (rs.next()) {
            return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    @Override
    public Optional<UserAccount> findByEmployeeId(long employeeId) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM user_account WHERE employee_id=?", employeeId);
        if (rs.next()) {
            return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    @Override
    public boolean updatePassword(long userAccountId, String newPasswordHash) throws SQLException {
        return SQLUtil.executeUpdate("UPDATE user_account SET password_hash=? WHERE user_account_id=?", newPasswordHash, userAccountId);
    }

    private UserAccount mapRow(ResultSet rs) throws SQLException {
        return new UserAccount(
                rs.getLong("user_account_id"),
                rs.getLong("employee_id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getString("access_role"),
                rs.getBoolean("active")
        );
    }
}
