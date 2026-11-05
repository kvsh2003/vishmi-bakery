package lk.ijse.vishmibakehouse.dao.custom;

import lk.ijse.vishmibakehouse.dao.CrudDAO;
import lk.ijse.vishmibakehouse.entity.IngredientPurchase;

import java.sql.SQLException;
import java.util.List;

public interface IngredientPurchaseDAO extends CrudDAO<IngredientPurchase, Long> {
    List<IngredientPurchase> findByIngredientId(long ingredientId) throws SQLException;
}
