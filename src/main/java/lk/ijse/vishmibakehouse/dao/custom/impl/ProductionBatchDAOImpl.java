package lk.ijse.vishmibakehouse.dao.custom.impl;

import lk.ijse.vishmibakehouse.dao.SQLUtil;
import lk.ijse.vishmibakehouse.dao.custom.ProductionBatchDAO;
import lk.ijse.vishmibakehouse.entity.ProductionBatch;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductionBatchDAOImpl implements ProductionBatchDAO {
    @Override
    public boolean save(ProductionBatch entity) throws SQLException {
        return saveAndReturnId(entity) > 0;
    }

    @Override
    public long saveAndReturnId(ProductionBatch entity) throws SQLException {
        return SQLUtil.executeInsertReturnGeneratedId(
                "INSERT INTO production_batch (product_id, recorded_by_employee_id, produced_on, planned_qty, actual_qty, status) VALUES (?, ?, ?, ?, ?, ?)",
                entity.getProductId(), entity.getRecordedByEmployeeId(), entity.getProducedOn(), entity.getPlannedQty(), entity.getActualQty(), entity.getStatus()
        );
    }

    @Override
    public boolean update(ProductionBatch entity) throws SQLException {
        return SQLUtil.executeUpdate(
                "UPDATE production_batch SET product_id=?, recorded_by_employee_id=?, produced_on=?, planned_qty=?, actual_qty=?, status=? WHERE production_batch_id=?",
                entity.getProductId(), entity.getRecordedByEmployeeId(), entity.getProducedOn(), entity.getPlannedQty(), entity.getActualQty(), entity.getStatus(), entity.getProductionBatchId()
        );
    }

    @Override
    public boolean delete(Long id) throws SQLException {
        return SQLUtil.executeUpdate("UPDATE production_batch SET status='CANCELLED' WHERE production_batch_id=?", id);
    }

    @Override
    public Optional<ProductionBatch> findById(Long id) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM production_batch WHERE production_batch_id=?", id);
        if (rs.next()) {
            return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    @Override
    public List<ProductionBatch> findAll() throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM production_batch ORDER BY produced_on DESC, production_batch_id DESC");
        List<ProductionBatch> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<ProductionBatch> findByDate(Date date) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM production_batch WHERE produced_on=? ORDER BY production_batch_id ASC", date);
        List<ProductionBatch> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<ProductionBatch> findByStatus(String status) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM production_batch WHERE status=? ORDER BY produced_on DESC", status);
        List<ProductionBatch> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    private ProductionBatch mapRow(ResultSet rs) throws SQLException {
        ProductionBatch pb = new ProductionBatch(
                rs.getLong("production_batch_id"),
                rs.getLong("product_id"),
                rs.getLong("recorded_by_employee_id"),
                rs.getDate("produced_on"),
                rs.getBigDecimal("planned_qty"),
                rs.getBigDecimal("actual_qty"),
                rs.getString("status")
        );
        pb.setCreatedAt(rs.getTimestamp("created_at"));
        return pb;
    }
}
