package lk.ijse.vishmibakehouse.dao.custom;

import lk.ijse.vishmibakehouse.dao.CrudDAO;
import lk.ijse.vishmibakehouse.entity.UserAccount;

import java.sql.SQLException;
import java.util.Optional;

public interface UserAccountDAO extends CrudDAO<UserAccount, Long> {
    Optional<UserAccount> findByUsername(String username) throws SQLException;
    Optional<UserAccount> findByEmployeeId(long employeeId) throws SQLException;
    boolean updatePassword(long userAccountId, String newPasswordHash) throws SQLException;
}
