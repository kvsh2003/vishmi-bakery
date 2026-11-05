package lk.ijse.vishmibakehouse.dao.custom.impl;

import lk.ijse.vishmibakehouse.dao.SQLUtil;
import lk.ijse.vishmibakehouse.dao.custom.IngredientPurchaseDAO;
import lk.ijse.vishmibakehouse.entity.IngredientPurchase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IngredientPurchaseDAOImpl implements IngredientPurchaseDAO {
    @Override
    public boolean save(IngredientPurchase entity) throws SQLException {
        return SQLUtil.executeUpdate(
                "INSERT INTO ingredient_purchase (ingredient_id, recorded_by_employee_id, purchased_at, qty, unit_cost, supplier_name) VALUES (?, ?, ?, ?, ?, ?)",
                entity.getIngredientId(), entity.getRecordedByEmployeeId(), entity.getPurchasedAt(), entity.getQty(), entity.getUnitCost(), entity.getSupplierName()
        );
    }

    @Override
    public boolean update(IngredientPurchase entity) throws SQLException {
        return SQLUtil.executeUpdate(
                "UPDATE ingredient_purchase SET ingredient_id=?, recorded_by_employee_id=?, purchased_at=?, qty=?, unit_cost=?, supplier_name=? WHERE ingredient_purchase_id=?",
                entity.getIngredientId(), entity.getRecordedByEmployeeId(), entity.getPurchasedAt(), entity.getQty(), entity.getUnitCost(), entity.getSupplierName(), entity.getIngredientPurchaseId()
        );
    }

    @Override
    public boolean delete(Long id) throws SQLException {
        return SQLUtil.executeUpdate("DELETE FROM ingredient_purchase WHERE ingredient_purchase_id=?", id);
    }

    @Override
    public Optional<IngredientPurchase> findById(Long id) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM ingredient_purchase WHERE ingredient_purchase_id=?", id);
        if (rs.next()) {
            return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    @Override
    public List<IngredientPurchase> findAll() throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM ingredient_purchase ORDER BY purchased_at DESC");
        List<IngredientPurchase> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<IngredientPurchase> findByIngredientId(long ingredientId) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM ingredient_purchase WHERE ingredient_id=? ORDER BY purchased_at DESC", ingredientId);
        List<IngredientPurchase> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    private IngredientPurchase mapRow(ResultSet rs) throws SQLException {
        IngredientPurchase ip = new IngredientPurchase(
                rs.getLong("ingredient_purchase_id"),
                rs.getLong("ingredient_id"),
                rs.getLong("recorded_by_employee_id"),
                rs.getTimestamp("purchased_at"),
                rs.getBigDecimal("qty"),
                rs.getBigDecimal("unit_cost"),
                rs.getString("supplier_name")
        );
        ip.setTotalCost(rs.getBigDecimal("total_cost"));
        ip.setCreatedAt(rs.getTimestamp("created_at"));
        return ip;
    }
}
