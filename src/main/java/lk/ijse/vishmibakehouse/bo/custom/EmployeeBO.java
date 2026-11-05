package lk.ijse.vishmibakehouse.bo.custom;

import lk.ijse.vishmibakehouse.bo.SuperBO;
import lk.ijse.vishmibakehouse.dto.EmployeeDTO;
import lk.ijse.vishmibakehouse.dto.UserAccountDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface EmployeeBO extends SuperBO {
    boolean saveEmployee(EmployeeDTO dto) throws SQLException;
    boolean updateEmployee(EmployeeDTO dto) throws SQLException;
    boolean deleteEmployee(long employeeId) throws SQLException;
    Optional<EmployeeDTO> findEmployeeById(long employeeId) throws SQLException;
    List<EmployeeDTO> getAllEmployees() throws SQLException;
    List<EmployeeDTO> getActiveEmployees() throws SQLException;

    boolean saveUserAccount(UserAccountDTO dto) throws SQLException;
    boolean updateUserAccount(UserAccountDTO dto) throws SQLException;
    boolean deleteUserAccount(long userAccountId) throws SQLException;
    List<UserAccountDTO> getAllUserAccounts() throws SQLException;
}
