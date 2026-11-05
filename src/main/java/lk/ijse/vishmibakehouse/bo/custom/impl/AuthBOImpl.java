package lk.ijse.vishmibakehouse.bo.custom.impl;

import lk.ijse.vishmibakehouse.bo.custom.AuthBO;
import lk.ijse.vishmibakehouse.dao.DAOFactory;
import lk.ijse.vishmibakehouse.dao.DAOTypes;
import lk.ijse.vishmibakehouse.dao.custom.EmployeeDAO;
import lk.ijse.vishmibakehouse.dao.custom.UserAccountDAO;
import lk.ijse.vishmibakehouse.dto.UserAccountDTO;
import lk.ijse.vishmibakehouse.entity.Employee;
import lk.ijse.vishmibakehouse.entity.UserAccount;
import lk.ijse.vishmibakehouse.util.PasswordHasher;

import java.sql.SQLException;
import java.util.Optional;

public class AuthBOImpl implements AuthBO {
    private final UserAccountDAO userAccountDAO = DAOFactory.getInstance().getDAO(DAOTypes.USER_ACCOUNT);
    private final EmployeeDAO employeeDAO = DAOFactory.getInstance().getDAO(DAOTypes.EMPLOYEE);

    @Override
    public Optional<UserAccountDTO> authenticate(String username, String plainPassword) throws SQLException {
        Optional<UserAccount> optUser = userAccountDAO.findByUsername(username);
        if (optUser.isEmpty()) {
            return Optional.empty();
        }

        UserAccount user = optUser.get();
        if (!user.isActive()) {
            return Optional.empty();
        }

        if (PasswordHasher.checkPassword(plainPassword, user.getPasswordHash())) {
            UserAccountDTO dto = new UserAccountDTO(
                    user.getUserAccountId(),
                    user.getEmployeeId(),
                    user.getUsername(),
                    user.getAccessRole(),
                    user.isActive()
            );

            Optional<Employee> emp = employeeDAO.findById(user.getEmployeeId());
            emp.ifPresent(e -> dto.setEmployeeName(e.getName()));

            return Optional.of(dto);
        }
        return Optional.empty();
    }

    @Override
    public boolean changePassword(long userAccountId, String oldPassword, String newPassword) throws SQLException {
        Optional<UserAccount> optUser = userAccountDAO.findById(userAccountId);
        if (optUser.isPresent()) {
            UserAccount user = optUser.get();
            if (PasswordHasher.checkPassword(oldPassword, user.getPasswordHash())) {
                String newHash = PasswordHasher.hashPassword(newPassword);
                return userAccountDAO.updatePassword(userAccountId, newHash);
            }
        }
        return false;
    }
}
