package lk.ijse.vishmibakehouse.dao.custom.impl;

import lk.ijse.vishmibakehouse.dao.SQLUtil;
import lk.ijse.vishmibakehouse.dao.custom.ProductDAO;
import lk.ijse.vishmibakehouse.entity.Product;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDAOImpl implements ProductDAO {
    @Override
    public boolean save(Product entity) throws SQLException {
        return SQLUtil.executeUpdate(
                "INSERT INTO product (name, category, unit, selling_price, active) VALUES (?, ?, ?, ?, ?)",
                entity.getName(), entity.getCategory(), entity.getUnit(), entity.getSellingPrice(), entity.isActive() ? 1 : 0
        );
    }

    @Override
    public boolean update(Product entity) throws SQLException {
        return SQLUtil.executeUpdate(
                "UPDATE product SET name=?, category=?, unit=?, selling_price=?, active=? WHERE product_id=?",
                entity.getName(), entity.getCategory(), entity.getUnit(), entity.getSellingPrice(), entity.isActive() ? 1 : 0, entity.getProductId()
        );
    }

    @Override
    public boolean delete(Long id) throws SQLException {
        return SQLUtil.executeUpdate("UPDATE product SET active=0 WHERE product_id=?", id);
    }

    @Override
    public Optional<Product> findById(Long id) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM product WHERE product_id=?", id);
        if (rs.next()) {
            return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    @Override
    public List<Product> findAll() throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM product ORDER BY product_id ASC");
        List<Product> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<Product> findByCategory(String category) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM product WHERE category=? AND active=1 ORDER BY name ASC", category);
        List<Product> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<Product> searchByName(String keyword) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM product WHERE name LIKE ? ORDER BY name ASC", "%" + keyword + "%");
        List<Product> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<Product> findActiveProducts() throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM product WHERE active=1 ORDER BY name ASC");
        List<Product> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        return new Product(
                rs.getLong("product_id"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getString("unit"),
                rs.getBigDecimal("selling_price"),
                rs.getBoolean("active")
        );
    }
}
