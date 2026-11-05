package lk.ijse.vishmibakehouse.dao.custom;

import lk.ijse.vishmibakehouse.dao.CrudDAO;
import lk.ijse.vishmibakehouse.entity.IngredientUsage;

import java.sql.SQLException;
import java.util.List;

public interface IngredientUsageDAO extends CrudDAO<IngredientUsage, Long> {
    List<IngredientUsage> findByProductionBatchId(long productionBatchId) throws SQLException;
    List<IngredientUsage> findByIngredientId(long ingredientId) throws SQLException;
}
