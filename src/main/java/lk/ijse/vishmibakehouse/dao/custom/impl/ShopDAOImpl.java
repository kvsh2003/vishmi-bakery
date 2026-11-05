package lk.ijse.vishmibakehouse.dao.custom.impl;

import lk.ijse.vishmibakehouse.dao.SQLUtil;
import lk.ijse.vishmibakehouse.dao.custom.ShopDAO;
import lk.ijse.vishmibakehouse.entity.Shop;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShopDAOImpl implements ShopDAO {
    @Override
    public boolean save(Shop entity) throws SQLException {
        return SQLUtil.executeUpdate(
                "INSERT INTO shop (name, phone, address, active) VALUES (?, ?, ?, ?)",
                entity.getName(), entity.getPhone(), entity.getAddress(), entity.isActive() ? 1 : 0
        );
    }

    @Override
    public boolean update(Shop entity) throws SQLException {
        return SQLUtil.executeUpdate(
                "UPDATE shop SET name=?, phone=?, address=?, active=? WHERE shop_id=?",
                entity.getName(), entity.getPhone(), entity.getAddress(), entity.isActive() ? 1 : 0, entity.getShopId()
        );
    }

    @Override
    public boolean delete(Long id) throws SQLException {
        return SQLUtil.executeUpdate("UPDATE shop SET active=0 WHERE shop_id=?", id);
    }

    @Override
    public Optional<Shop> findById(Long id) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM shop WHERE shop_id=?", id);
        if (rs.next()) {
            return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    @Override
    public List<Shop> findAll() throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM shop ORDER BY shop_id ASC");
        List<Shop> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<Shop> findActiveShops() throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM shop WHERE active=1 ORDER BY name ASC");
        List<Shop> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    private Shop mapRow(ResultSet rs) throws SQLException {
        return new Shop(
                rs.getLong("shop_id"),
                rs.getString("name"),
                rs.getString("phone"),
                rs.getString("address"),
                rs.getBoolean("active")
        );
    }
}
