package lk.ijse.vishmibakehouse.dao.custom.impl;

import lk.ijse.vishmibakehouse.dao.SQLUtil;
import lk.ijse.vishmibakehouse.dao.custom.IngredientUsageDAO;
import lk.ijse.vishmibakehouse.entity.IngredientUsage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IngredientUsageDAOImpl implements IngredientUsageDAO {
    @Override
    public boolean save(IngredientUsage entity) throws SQLException {
        return SQLUtil.executeUpdate(
                "INSERT INTO ingredient_usage (production_batch_id, ingredient_id, recorded_by_employee_id, used_at, qty) VALUES (?, ?, ?, ?, ?)",
                entity.getProductionBatchId(), entity.getIngredientId(), entity.getRecordedByEmployeeId(), entity.getUsedAt(), entity.getQty()
        );
    }

    @Override
    public boolean update(IngredientUsage entity) throws SQLException {
        return SQLUtil.executeUpdate(
                "UPDATE ingredient_usage SET production_batch_id=?, ingredient_id=?, recorded_by_employee_id=?, used_at=?, qty=? WHERE ingredient_usage_id=?",
                entity.getProductionBatchId(), entity.getIngredientId(), entity.getRecordedByEmployeeId(), entity.getUsedAt(), entity.getQty(), entity.getIngredientUsageId()
        );
    }

    @Override
    public boolean delete(Long id) throws SQLException {
        return SQLUtil.executeUpdate("DELETE FROM ingredient_usage WHERE ingredient_usage_id=?", id);
    }

    @Override
    public Optional<IngredientUsage> findById(Long id) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM ingredient_usage WHERE ingredient_usage_id=?", id);
        if (rs.next()) {
            return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    @Override
    public List<IngredientUsage> findAll() throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM ingredient_usage ORDER BY used_at DESC");
        List<IngredientUsage> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<IngredientUsage> findByProductionBatchId(long productionBatchId) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM ingredient_usage WHERE production_batch_id=? ORDER BY used_at ASC", productionBatchId);
        List<IngredientUsage> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<IngredientUsage> findByIngredientId(long ingredientId) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM ingredient_usage WHERE ingredient_id=? ORDER BY used_at DESC", ingredientId);
        List<IngredientUsage> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    private IngredientUsage mapRow(ResultSet rs) throws SQLException {
        IngredientUsage iu = new IngredientUsage(
                rs.getLong("ingredient_usage_id"),
                rs.getLong("production_batch_id"),
                rs.getLong("ingredient_id"),
                rs.getLong("recorded_by_employee_id"),
                rs.getTimestamp("used_at"),
                rs.getBigDecimal("qty")
        );
        iu.setCreatedAt(rs.getTimestamp("created_at"));
        return iu;
    }
}
