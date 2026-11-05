package lk.ijse.vishmibakehouse.bo.custom.impl;

import lk.ijse.vishmibakehouse.bo.custom.EmployeeBO;
import lk.ijse.vishmibakehouse.dao.DAOFactory;
import lk.ijse.vishmibakehouse.dao.DAOTypes;
import lk.ijse.vishmibakehouse.dao.custom.EmployeeDAO;
import lk.ijse.vishmibakehouse.dao.custom.UserAccountDAO;
import lk.ijse.vishmibakehouse.dto.EmployeeDTO;
import lk.ijse.vishmibakehouse.dto.UserAccountDTO;
import lk.ijse.vishmibakehouse.entity.Employee;
import lk.ijse.vishmibakehouse.entity.UserAccount;
import lk.ijse.vishmibakehouse.util.PasswordHasher;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeBOImpl implements EmployeeBO {
    private final EmployeeDAO employeeDAO = DAOFactory.getInstance().getDAO(DAOTypes.EMPLOYEE);
    private final UserAccountDAO userAccountDAO = DAOFactory.getInstance().getDAO(DAOTypes.USER_ACCOUNT);

    @Override
    public boolean saveEmployee(EmployeeDTO dto) throws SQLException {
        return employeeDAO.save(new Employee(
                0, dto.getName(), dto.getPhone(), dto.getAddress(), dto.getJobRole(), dto.isActive()
        ));
    }

    @Override
    public boolean updateEmployee(EmployeeDTO dto) throws SQLException {
        return employeeDAO.update(new Employee(
                dto.getEmployeeId(), dto.getName(), dto.getPhone(), dto.getAddress(), dto.getJobRole(), dto.isActive()
        ));
    }

    @Override
    public boolean deleteEmployee(long employeeId) throws SQLException {
        return employeeDAO.delete(employeeId);
    }

    @Override
    public Optional<EmployeeDTO> findEmployeeById(long employeeId) throws SQLException {
        return employeeDAO.findById(employeeId).map(e -> new EmployeeDTO(
                e.getEmployeeId(), e.getName(), e.getPhone(), e.getAddress(), e.getJobRole(), e.isActive()
        ));
    }

    @Override
    public List<EmployeeDTO> getAllEmployees() throws SQLException {
        List<Employee> list = employeeDAO.findAll();
        List<EmployeeDTO> dtos = new ArrayList<>();
        for (Employee e : list) {
            dtos.add(new EmployeeDTO(
                    e.getEmployeeId(), e.getName(), e.getPhone(), e.getAddress(), e.getJobRole(), e.isActive()
            ));
        }
        return dtos;
    }

    @Override
    public List<EmployeeDTO> getActiveEmployees() throws SQLException {
        List<Employee> list = employeeDAO.findActiveEmployees();
        List<EmployeeDTO> dtos = new ArrayList<>();
        for (Employee e : list) {
            dtos.add(new EmployeeDTO(
                    e.getEmployeeId(), e.getName(), e.getPhone(), e.getAddress(), e.getJobRole(), e.isActive()
            ));
        }
        return dtos;
    }

    @Override
    public boolean saveUserAccount(UserAccountDTO dto) throws SQLException {
        String hash = PasswordHasher.hashPassword(dto.getPassword() != null ? dto.getPassword() : "12345678");
        return userAccountDAO.save(new UserAccount(
                0, dto.getEmployeeId(), dto.getUsername(), hash, dto.getAccessRole(), dto.isActive()
        ));
    }

    @Override
    public boolean updateUserAccount(UserAccountDTO dto) throws SQLException {
        boolean ok = userAccountDAO.update(new UserAccount(
                dto.getUserAccountId(), dto.getEmployeeId(), dto.getUsername(), "", dto.getAccessRole(), dto.isActive()
        ));
        if (ok && dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            userAccountDAO.updatePassword(dto.getUserAccountId(), PasswordHasher.hashPassword(dto.getPassword()));
        }
        return ok;
    }

    @Override
    public boolean deleteUserAccount(long userAccountId) throws SQLException {
        return userAccountDAO.delete(userAccountId);
    }

    @Override
    public List<UserAccountDTO> getAllUserAccounts() throws SQLException {
        List<UserAccount> list = userAccountDAO.findAll();
        List<UserAccountDTO> dtos = new ArrayList<>();
        for (UserAccount u : list) {
            UserAccountDTO dto = new UserAccountDTO(
                    u.getUserAccountId(), u.getEmployeeId(), u.getUsername(), u.getAccessRole(), u.isActive()
            );
            employeeDAO.findById(u.getEmployeeId()).ifPresent(e -> dto.setEmployeeName(e.getName()));
            dtos.add(dto);
        }
        return dtos;
    }
}
