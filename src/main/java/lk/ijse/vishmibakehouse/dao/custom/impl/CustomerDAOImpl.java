package lk.ijse.vishmibakehouse.dao.custom.impl;

import lk.ijse.vishmibakehouse.dao.SQLUtil;
import lk.ijse.vishmibakehouse.dao.custom.CustomerDAO;
import lk.ijse.vishmibakehouse.entity.Customer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerDAOImpl implements CustomerDAO {
    @Override
    public boolean save(Customer entity) throws SQLException {
        return SQLUtil.executeUpdate(
                "INSERT INTO customer (name, phone, address) VALUES (?, ?, ?)",
                entity.getName(), entity.getPhone(), entity.getAddress()
        );
    }

    @Override
    public boolean update(Customer entity) throws SQLException {
        return SQLUtil.executeUpdate(
                "UPDATE customer SET name=?, phone=?, address=? WHERE customer_id=?",
                entity.getName(), entity.getPhone(), entity.getAddress(), entity.getCustomerId()
        );
    }

    @Override
    public boolean delete(Long id) throws SQLException {
        return SQLUtil.executeUpdate("DELETE FROM customer WHERE customer_id=?", id);
    }

    @Override
    public Optional<Customer> findById(Long id) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM customer WHERE customer_id=?", id);
        if (rs.next()) {
            return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    @Override
    public List<Customer> findAll() throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM customer ORDER BY customer_id ASC");
        List<Customer> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public Optional<Customer> findByPhone(String phone) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM customer WHERE phone=?", phone);
        if (rs.next()) {
            return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    @Override
    public List<Customer> searchByName(String keyword) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM customer WHERE name LIKE ? OR phone LIKE ? ORDER BY name ASC", "%" + keyword + "%", "%" + keyword + "%");
        List<Customer> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getLong("customer_id"),
                rs.getString("name"),
                rs.getString("phone"),
                rs.getString("address")
        );
    }
}
