package lk.ijse.vishmibakehouse.dao.custom.impl;

import lk.ijse.vishmibakehouse.dao.SQLUtil;
import lk.ijse.vishmibakehouse.dao.custom.SpecialOrderDAO;
import lk.ijse.vishmibakehouse.entity.SpecialOrder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpecialOrderDAOImpl implements SpecialOrderDAO {
    @Override
    public boolean save(SpecialOrder entity) throws SQLException {
        return saveAndReturnId(entity) > 0;
    }

    @Override
    public long saveAndReturnId(SpecialOrder entity) throws SQLException {
        return SQLUtil.executeInsertReturnGeneratedId(
                "INSERT INTO special_order (customer_id, recorded_by_employee_id, order_type, required_at, status, instructions, deposit_amount) VALUES (?, ?, ?, ?, ?, ?, ?)",
                entity.getCustomerId(), entity.getRecordedByEmployeeId(), entity.getOrderType(), entity.getRequiredAt(), entity.getStatus(), entity.getInstructions(), entity.getDepositAmount()
        );
    }

    @Override
    public boolean update(SpecialOrder entity) throws SQLException {
        return SQLUtil.executeUpdate(
                "UPDATE special_order SET customer_id=?, recorded_by_employee_id=?, order_type=?, required_at=?, status=?, instructions=?, deposit_amount=? WHERE special_order_id=?",
                entity.getCustomerId(), entity.getRecordedByEmployeeId(), entity.getOrderType(), entity.getRequiredAt(), entity.getStatus(), entity.getInstructions(), entity.getDepositAmount(), entity.getSpecialOrderId()
        );
    }

    @Override
    public boolean updateStatus(long specialOrderId, String status) throws SQLException {
        return SQLUtil.executeUpdate("UPDATE special_order SET status=? WHERE special_order_id=?", status, specialOrderId);
    }

    @Override
    public boolean delete(Long id) throws SQLException {
        return updateStatus(id, "CANCELLED");
    }

    @Override
    public Optional<SpecialOrder> findById(Long id) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM special_order WHERE special_order_id=?", id);
        if (rs.next()) {
            return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    @Override
    public List<SpecialOrder> findAll() throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM special_order ORDER BY required_at ASC");
        List<SpecialOrder> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<SpecialOrder> findByCustomerId(long customerId) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM special_order WHERE customer_id=? ORDER BY required_at ASC", customerId);
        List<SpecialOrder> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<SpecialOrder> findByStatus(String status) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM special_order WHERE status=? ORDER BY required_at ASC", status);
        List<SpecialOrder> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    private SpecialOrder mapRow(ResultSet rs) throws SQLException {
        SpecialOrder so = new SpecialOrder(
                rs.getLong("special_order_id"),
                rs.getLong("customer_id"),
                rs.getLong("recorded_by_employee_id"),
                rs.getString("order_type"),
                rs.getTimestamp("required_at"),
                rs.getString("status"),
                rs.getString("instructions"),
                rs.getBigDecimal("deposit_amount")
        );
        so.setCreatedAt(rs.getTimestamp("created_at"));
        return so;
    }
}
