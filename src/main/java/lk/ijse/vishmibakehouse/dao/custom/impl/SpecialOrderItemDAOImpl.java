package lk.ijse.vishmibakehouse.dao.custom.impl;

import lk.ijse.vishmibakehouse.dao.SQLUtil;
import lk.ijse.vishmibakehouse.dao.custom.SpecialOrderItemDAO;
import lk.ijse.vishmibakehouse.entity.SpecialOrderItem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpecialOrderItemDAOImpl implements SpecialOrderItemDAO {
    @Override
    public boolean save(SpecialOrderItem entity) throws SQLException {
        return SQLUtil.executeUpdate(
                "INSERT INTO special_order_item (special_order_id, product_id, description, quantity, unit_price) VALUES (?, ?, ?, ?, ?)",
                entity.getSpecialOrderId(), entity.getProductId(), entity.getDescription(), entity.getQuantity(), entity.getUnitPrice()
        );
    }

    @Override
    public boolean saveAll(List<SpecialOrderItem> items) throws SQLException {
        for (SpecialOrderItem item : items) {
            if (!save(item)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean update(SpecialOrderItem entity) throws SQLException {
        return SQLUtil.executeUpdate(
                "UPDATE special_order_item SET special_order_id=?, product_id=?, description=?, quantity=?, unit_price=? WHERE special_order_item_id=?",
                entity.getSpecialOrderId(), entity.getProductId(), entity.getDescription(), entity.getQuantity(), entity.getUnitPrice(), entity.getSpecialOrderItemId()
        );
    }

    @Override
    public boolean delete(Long id) throws SQLException {
        return SQLUtil.executeUpdate("DELETE FROM special_order_item WHERE special_order_item_id=?", id);
    }

    @Override
    public Optional<SpecialOrderItem> findById(Long id) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM special_order_item WHERE special_order_item_id=?", id);
        if (rs.next()) {
            return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    @Override
    public List<SpecialOrderItem> findAll() throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM special_order_item");
        List<SpecialOrderItem> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<SpecialOrderItem> findBySpecialOrderId(long specialOrderId) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM special_order_item WHERE special_order_id=? ORDER BY special_order_item_id ASC", specialOrderId);
        List<SpecialOrderItem> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    private SpecialOrderItem mapRow(ResultSet rs) throws SQLException {
        Long productId = rs.getObject("product_id") != null ? rs.getLong("product_id") : null;
        SpecialOrderItem soi = new SpecialOrderItem(
                rs.getLong("special_order_item_id"),
                rs.getLong("special_order_id"),
                productId,
                rs.getString("description"),
                rs.getBigDecimal("quantity"),
                rs.getBigDecimal("unit_price")
        );
        soi.setLineTotal(rs.getBigDecimal("line_total"));
        return soi;
    }
}
