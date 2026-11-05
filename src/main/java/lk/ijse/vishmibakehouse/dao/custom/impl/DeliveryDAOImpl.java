package lk.ijse.vishmibakehouse.dao.custom.impl;

import lk.ijse.vishmibakehouse.dao.SQLUtil;
import lk.ijse.vishmibakehouse.dao.custom.DeliveryDAO;
import lk.ijse.vishmibakehouse.entity.Delivery;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DeliveryDAOImpl implements DeliveryDAO {
    @Override
    public boolean save(Delivery entity) throws SQLException {
        return saveAndReturnId(entity) > 0;
    }

    @Override
    public long saveAndReturnId(Delivery entity) throws SQLException {
        return SQLUtil.executeInsertReturnGeneratedId(
                "INSERT INTO delivery (shop_id, vehicle_id, delivery_employee_id, packed_by_employee_id, delivery_date, status) VALUES (?, ?, ?, ?, ?, ?)",
                entity.getShopId(), entity.getVehicleId(), entity.getDeliveryEmployeeId(), entity.getPackedByEmployeeId(), entity.getDeliveryDate(), entity.getStatus()
        );
    }

    @Override
    public boolean update(Delivery entity) throws SQLException {
        return SQLUtil.executeUpdate(
                "UPDATE delivery SET shop_id=?, vehicle_id=?, delivery_employee_id=?, packed_by_employee_id=?, delivery_date=?, status=? WHERE delivery_id=?",
                entity.getShopId(), entity.getVehicleId(), entity.getDeliveryEmployeeId(), entity.getPackedByEmployeeId(), entity.getDeliveryDate(), entity.getStatus(), entity.getDeliveryId()
        );
    }

    @Override
    public boolean updateStatusAndVehicle(long deliveryId, String status, Long vehicleId, Long deliveryEmployeeId) throws SQLException {
        return SQLUtil.executeUpdate(
                "UPDATE delivery SET status=?, vehicle_id=?, delivery_employee_id=? WHERE delivery_id=?",
                status, vehicleId, deliveryEmployeeId, deliveryId
        );
    }

    @Override
    public boolean delete(Long id) throws SQLException {
        return SQLUtil.executeUpdate("UPDATE delivery SET status='CANCELLED' WHERE delivery_id=?", id);
    }

    @Override
    public Optional<Delivery> findById(Long id) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM delivery WHERE delivery_id=?", id);
        if (rs.next()) {
            return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    @Override
    public List<Delivery> findAll() throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM delivery ORDER BY delivery_date DESC, delivery_id DESC");
        List<Delivery> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<Delivery> findByDate(Date date) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM delivery WHERE delivery_date=? ORDER BY delivery_id ASC", date);
        List<Delivery> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<Delivery> findByStatus(String status) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM delivery WHERE status=? ORDER BY delivery_date DESC", status);
        List<Delivery> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    private Delivery mapRow(ResultSet rs) throws SQLException {
        Long vehicleId = rs.getObject("vehicle_id") != null ? rs.getLong("vehicle_id") : null;
        Long deliveryEmployeeId = rs.getObject("delivery_employee_id") != null ? rs.getLong("delivery_employee_id") : null;
        Delivery d = new Delivery(
                rs.getLong("delivery_id"),
                rs.getLong("shop_id"),
                vehicleId,
                deliveryEmployeeId,
                rs.getLong("packed_by_employee_id"),
                rs.getDate("delivery_date"),
                rs.getString("status")
        );
        d.setCreatedAt(rs.getTimestamp("created_at"));
        return d;
    }
}
