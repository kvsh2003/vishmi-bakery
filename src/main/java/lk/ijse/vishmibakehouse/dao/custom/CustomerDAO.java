package lk.ijse.vishmibakehouse.dao.custom;

import lk.ijse.vishmibakehouse.dao.CrudDAO;
import lk.ijse.vishmibakehouse.entity.Customer;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CustomerDAO extends CrudDAO<Customer, Long> {
    Optional<Customer> findByPhone(String phone) throws SQLException;
    List<Customer> searchByName(String keyword) throws SQLException;
}
