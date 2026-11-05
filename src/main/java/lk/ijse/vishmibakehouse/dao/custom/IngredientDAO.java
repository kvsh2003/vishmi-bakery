package lk.ijse.vishmibakehouse.dao.custom;

import lk.ijse.vishmibakehouse.dao.CrudDAO;
import lk.ijse.vishmibakehouse.entity.Ingredient;

import java.sql.SQLException;
import java.util.List;

public interface IngredientDAO extends CrudDAO<Ingredient, Long> {
    List<Ingredient> findActiveIngredients() throws SQLException;
}
