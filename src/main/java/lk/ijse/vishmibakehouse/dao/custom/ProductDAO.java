package lk.ijse.vishmibakehouse.dao.custom;

import lk.ijse.vishmibakehouse.dao.CrudDAO;
import lk.ijse.vishmibakehouse.entity.Product;

import java.sql.SQLException;
import java.util.List;

public interface ProductDAO extends CrudDAO<Product, Long> {
    List<Product> findByCategory(String category) throws SQLException;
    List<Product> searchByName(String keyword) throws SQLException;
    List<Product> findActiveProducts() throws SQLException;
}
