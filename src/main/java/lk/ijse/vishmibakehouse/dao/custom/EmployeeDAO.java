package lk.ijse.vishmibakehouse.dao.custom;

import lk.ijse.vishmibakehouse.dao.CrudDAO;
import lk.ijse.vishmibakehouse.entity.Employee;

import java.sql.SQLException;
import java.util.List;

public interface EmployeeDAO extends CrudDAO<Employee, Long> {
    List<Employee> findByRole(String role) throws SQLException;
    List<Employee> findActiveEmployees() throws SQLException;
}
