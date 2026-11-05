package lk.ijse.vishmibakehouse.bo.custom;

import lk.ijse.vishmibakehouse.bo.SuperBO;
import lk.ijse.vishmibakehouse.dto.UserAccountDTO;

import java.sql.SQLException;
import java.util.Optional;

public interface AuthBO extends SuperBO {
    Optional<UserAccountDTO> authenticate(String username, String plainPassword) throws SQLException;
    boolean changePassword(long userAccountId, String oldPassword, String newPassword) throws SQLException;
}
