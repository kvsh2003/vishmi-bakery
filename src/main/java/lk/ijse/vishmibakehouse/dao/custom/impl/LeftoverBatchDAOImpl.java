package lk.ijse.vishmibakehouse.dao.custom.impl;

import lk.ijse.vishmibakehouse.dao.SQLUtil;
import lk.ijse.vishmibakehouse.dao.custom.LeftoverBatchDAO;
import lk.ijse.vishmibakehouse.entity.LeftoverBatch;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LeftoverBatchDAOImpl implements LeftoverBatchDAO {
    @Override
    public boolean save(LeftoverBatch entity) throws SQLException {
        return saveAndReturnId(entity) > 0;
    }

    @Override
    public long saveAndReturnId(LeftoverBatch entity) throws SQLException {
        return SQLUtil.executeInsertReturnGeneratedId(
                "INSERT INTO leftover_batch (production_batch_id, recorded_by_employee_id, recorded_on, qty_pieces) VALUES (?, ?, ?, ?)",
                entity.getProductionBatchId(), entity.getRecordedByEmployeeId(), entity.getRecordedOn(), entity.getQtyPieces()
        );
    }

    @Override
    public boolean update(LeftoverBatch entity) throws SQLException {
        return SQLUtil.executeUpdate(
                "UPDATE leftover_batch SET production_batch_id=?, recorded_by_employee_id=?, recorded_on=?, qty_pieces=? WHERE leftover_batch_id=?",
                entity.getProductionBatchId(), entity.getRecordedByEmployeeId(), entity.getRecordedOn(), entity.getQtyPieces(), entity.getLeftoverBatchId()
        );
    }

    @Override
    public boolean delete(Long id) throws SQLException {
        return SQLUtil.executeUpdate("DELETE FROM leftover_batch WHERE leftover_batch_id=?", id);
    }

    @Override
    public Optional<LeftoverBatch> findById(Long id) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM leftover_batch WHERE leftover_batch_id=?", id);
        if (rs.next()) {
            return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    @Override
    public List<LeftoverBatch> findAll() throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM leftover_batch ORDER BY recorded_on DESC, leftover_batch_id DESC");
        List<LeftoverBatch> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<LeftoverBatch> findByProductionBatchId(long productionBatchId) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM leftover_batch WHERE production_batch_id=? ORDER BY recorded_on DESC", productionBatchId);
        List<LeftoverBatch> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    private LeftoverBatch mapRow(ResultSet rs) throws SQLException {
        LeftoverBatch lb = new LeftoverBatch(
                rs.getLong("leftover_batch_id"),
                rs.getLong("production_batch_id"),
                rs.getLong("recorded_by_employee_id"),
                rs.getDate("recorded_on"),
                rs.getInt("qty_pieces")
        );
        lb.setCreatedAt(rs.getTimestamp("created_at"));
        return lb;
    }
}
