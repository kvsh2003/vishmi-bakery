package lk.ijse.vishmibakehouse.dao.custom.impl;

import lk.ijse.vishmibakehouse.dao.SQLUtil;
import lk.ijse.vishmibakehouse.dao.custom.IngredientDAO;
import lk.ijse.vishmibakehouse.entity.Ingredient;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IngredientDAOImpl implements IngredientDAO {
    @Override
    public boolean save(Ingredient entity) throws SQLException {
        return SQLUtil.executeUpdate(
                "INSERT INTO ingredient (name, unit, reorder_level, active) VALUES (?, ?, ?, ?)",
                entity.getName(), entity.getUnit(), entity.getReorderLevel(), entity.isActive() ? 1 : 0
        );
    }

    @Override
    public boolean update(Ingredient entity) throws SQLException {
        return SQLUtil.executeUpdate(
                "UPDATE ingredient SET name=?, unit=?, reorder_level=?, active=? WHERE ingredient_id=?",
                entity.getName(), entity.getUnit(), entity.getReorderLevel(), entity.isActive() ? 1 : 0, entity.getIngredientId()
        );
    }

    @Override
    public boolean delete(Long id) throws SQLException {
        return SQLUtil.executeUpdate("UPDATE ingredient SET active=0 WHERE ingredient_id=?", id);
    }

    @Override
    public Optional<Ingredient> findById(Long id) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM ingredient WHERE ingredient_id=?", id);
        if (rs.next()) {
            return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    @Override
    public List<Ingredient> findAll() throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM ingredient ORDER BY ingredient_id ASC");
        List<Ingredient> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<Ingredient> findActiveIngredients() throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM ingredient WHERE active=1 ORDER BY name ASC");
        List<Ingredient> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    private Ingredient mapRow(ResultSet rs) throws SQLException {
        return new Ingredient(
                rs.getLong("ingredient_id"),
                rs.getString("name"),
                rs.getString("unit"),
                rs.getBigDecimal("reorder_level"),
                rs.getBoolean("active")
        );
    }
}
