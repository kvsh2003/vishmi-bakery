package lk.ijse.vishmibakehouse.dao.custom.impl;

import lk.ijse.vishmibakehouse.dao.SQLUtil;
import lk.ijse.vishmibakehouse.dao.custom.DeliveryItemDAO;
import lk.ijse.vishmibakehouse.entity.DeliveryItem;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DeliveryItemDAOImpl implements DeliveryItemDAO {
    @Override
    public boolean save(DeliveryItem entity) throws SQLException {
        return SQLUtil.executeUpdate(
                "INSERT INTO delivery_item (delivery_id, product_id, packed_qty, delivered_qty) VALUES (?, ?, ?, ?)",
                entity.getDeliveryId(), entity.getProductId(), entity.getPackedQty(), entity.getDeliveredQty()
        );
    }

    @Override
    public boolean saveAll(List<DeliveryItem> items) throws SQLException {
        for (DeliveryItem item : items) {
            if (!save(item)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean update(DeliveryItem entity) throws SQLException {
        return SQLUtil.executeUpdate(
                "UPDATE delivery_item SET delivery_id=?, product_id=?, packed_qty=?, delivered_qty=? WHERE delivery_item_id=?",
                entity.getDeliveryId(), entity.getProductId(), entity.getPackedQty(), entity.getDeliveredQty(), entity.getDeliveryItemId()
        );
    }

    @Override
    public boolean updateDeliveredQty(long deliveryItemId, BigDecimal deliveredQty) throws SQLException {
        return SQLUtil.executeUpdate("UPDATE delivery_item SET delivered_qty=? WHERE delivery_item_id=?", deliveredQty, deliveryItemId);
    }

    @Override
    public boolean delete(Long id) throws SQLException {
        return SQLUtil.executeUpdate("DELETE FROM delivery_item WHERE delivery_item_id=?", id);
    }

    @Override
    public Optional<DeliveryItem> findById(Long id) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM delivery_item WHERE delivery_item_id=?", id);
        if (rs.next()) {
            return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    @Override
    public List<DeliveryItem> findAll() throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM delivery_item");
        List<DeliveryItem> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<DeliveryItem> findByDeliveryId(long deliveryId) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM delivery_item WHERE delivery_id=? ORDER BY delivery_item_id ASC", deliveryId);
        List<DeliveryItem> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    private DeliveryItem mapRow(ResultSet rs) throws SQLException {
        return new DeliveryItem(
                rs.getLong("delivery_item_id"),
                rs.getLong("delivery_id"),
                rs.getLong("product_id"),
                rs.getBigDecimal("packed_qty"),
                rs.getBigDecimal("delivered_qty")
        );
    }
}
