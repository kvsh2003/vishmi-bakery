package lk.ijse.vishmibakehouse.bo.custom;

import lk.ijse.vishmibakehouse.bo.SuperBO;
import lk.ijse.vishmibakehouse.dto.IngredientDTO;
import lk.ijse.vishmibakehouse.dto.IngredientPurchaseDTO;
import lk.ijse.vishmibakehouse.dto.IngredientStockDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IngredientBO extends SuperBO {
    boolean saveIngredient(IngredientDTO dto) throws SQLException;
    boolean updateIngredient(IngredientDTO dto) throws SQLException;
    boolean deleteIngredient(long ingredientId) throws SQLException;
    Optional<IngredientDTO> findIngredientById(long ingredientId) throws SQLException;
    List<IngredientDTO> getAllIngredients() throws SQLException;
    List<IngredientDTO> getActiveIngredients() throws SQLException;

    boolean recordPurchase(IngredientPurchaseDTO purchaseDTO) throws SQLException;
    List<IngredientPurchaseDTO> getAllPurchases() throws SQLException;

    List<IngredientStockDTO> getIngredientStockList() throws SQLException;
    List<IngredientStockDTO> getLowStockAlerts() throws SQLException;
}
