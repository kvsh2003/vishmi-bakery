package lk.ijse.vishmibakehouse.dao.custom.impl;

import lk.ijse.vishmibakehouse.dao.SQLUtil;
import lk.ijse.vishmibakehouse.dao.custom.BreadCrumbBatchDAO;
import lk.ijse.vishmibakehouse.entity.BreadCrumbBatch;
import lk.ijse.vishmibakehouse.entity.BreadCrumbInput;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BreadCrumbBatchDAOImpl implements BreadCrumbBatchDAO {
    @Override
    public boolean save(BreadCrumbBatch entity) throws SQLException {
        return saveAndReturnId(entity) > 0;
    }

    @Override
    public long saveAndReturnId(BreadCrumbBatch entity) throws SQLException {
        return SQLUtil.executeInsertReturnGeneratedId(
                "INSERT INTO bread_crumb_batch (output_product_id, recorded_by_employee_id, produced_at, output_qty_kg) VALUES (?, ?, ?, ?)",
                entity.getOutputProductId(), entity.getRecordedByEmployeeId(), entity.getProducedAt(), entity.getOutputQtyKg()
        );
    }

    @Override
    public boolean saveBreadCrumbInput(BreadCrumbInput input) throws SQLException {
        return SQLUtil.executeUpdate(
                "INSERT INTO bread_crumb_input (bread_crumb_batch_id, leftover_batch_id, input_qty_pieces) VALUES (?, ?, ?)",
                input.getBreadCrumbBatchId(), input.getLeftoverBatchId(), input.getInputQtyPieces()
        );
    }

    @Override
    public List<BreadCrumbInput> findInputsByBatchId(long breadCrumbBatchId) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM bread_crumb_input WHERE bread_crumb_batch_id=?", breadCrumbBatchId);
        List<BreadCrumbInput> list = new ArrayList<>();
        while (rs.next()) {
            list.add(new BreadCrumbInput(
                    rs.getLong("bread_crumb_batch_id"),
                    rs.getLong("leftover_batch_id"),
                    rs.getInt("input_qty_pieces")
            ));
        }
        return list;
    }

    @Override
    public boolean update(BreadCrumbBatch entity) throws SQLException {
        return SQLUtil.executeUpdate(
                "UPDATE bread_crumb_batch SET output_product_id=?, recorded_by_employee_id=?, produced_at=?, output_qty_kg=? WHERE bread_crumb_batch_id=?",
                entity.getOutputProductId(), entity.getRecordedByEmployeeId(), entity.getProducedAt(), entity.getOutputQtyKg(), entity.getBreadCrumbBatchId()
        );
    }

    @Override
    public boolean delete(Long id) throws SQLException {
        return SQLUtil.executeUpdate("DELETE FROM bread_crumb_batch WHERE bread_crumb_batch_id=?", id);
    }

    @Override
    public Optional<BreadCrumbBatch> findById(Long id) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM bread_crumb_batch WHERE bread_crumb_batch_id=?", id);
        if (rs.next()) {
            return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    @Override
    public List<BreadCrumbBatch> findAll() throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM bread_crumb_batch ORDER BY produced_at DESC");
        List<BreadCrumbBatch> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    private BreadCrumbBatch mapRow(ResultSet rs) throws SQLException {
        BreadCrumbBatch bcb = new BreadCrumbBatch(
                rs.getLong("bread_crumb_batch_id"),
                rs.getLong("output_product_id"),
                rs.getLong("recorded_by_employee_id"),
                rs.getTimestamp("produced_at"),
                rs.getBigDecimal("output_qty_kg")
        );
        bcb.setCreatedAt(rs.getTimestamp("created_at"));
        return bcb;
    }
}
