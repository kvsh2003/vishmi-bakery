package lk.ijse.vishmibakehouse.dao.custom.impl;

import lk.ijse.vishmibakehouse.dao.SQLUtil;
import lk.ijse.vishmibakehouse.dao.custom.EmployeeDAO;
import lk.ijse.vishmibakehouse.entity.Employee;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeDAOImpl implements EmployeeDAO {
    @Override
    public boolean save(Employee entity) throws SQLException {
        return SQLUtil.executeUpdate(
                "INSERT INTO employee (name, phone, address, job_role, active) VALUES (?, ?, ?, ?, ?)",
                entity.getName(), entity.getPhone(), entity.getAddress(), entity.getJobRole(), entity.isActive() ? 1 : 0
        );
    }

    @Override
    public boolean update(Employee entity) throws SQLException {
        return SQLUtil.executeUpdate(
                "UPDATE employee SET name=?, phone=?, address=?, job_role=?, active=? WHERE employee_id=?",
                entity.getName(), entity.getPhone(), entity.getAddress(), entity.getJobRole(), entity.isActive() ? 1 : 0, entity.getEmployeeId()
        );
    }

    @Override
    public boolean delete(Long id) throws SQLException {
        return SQLUtil.executeUpdate("UPDATE employee SET active=0 WHERE employee_id=?", id);
    }

    @Override
    public Optional<Employee> findById(Long id) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM employee WHERE employee_id=?", id);
        if (rs.next()) {
            return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    @Override
    public List<Employee> findAll() throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM employee ORDER BY employee_id ASC");
        List<Employee> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<Employee> findByRole(String role) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM employee WHERE job_role=? AND active=1 ORDER BY name ASC", role);
        List<Employee> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<Employee> findActiveEmployees() throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM employee WHERE active=1 ORDER BY name ASC");
        List<Employee> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    private Employee mapRow(ResultSet rs) throws SQLException {
        return new Employee(
                rs.getLong("employee_id"),
                rs.getString("name"),
                rs.getString("phone"),
                rs.getString("address"),
                rs.getString("job_role"),
                rs.getBoolean("active"),
                rs.getTimestamp("created_at")
        );
    }
}
