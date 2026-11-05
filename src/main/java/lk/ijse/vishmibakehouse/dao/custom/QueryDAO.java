package lk.ijse.vishmibakehouse.dao.custom;

import lk.ijse.vishmibakehouse.dao.SuperDAO;
import lk.ijse.vishmibakehouse.dto.DashboardSummaryDTO;
import lk.ijse.vishmibakehouse.dto.DeliverySummaryDTO;
import lk.ijse.vishmibakehouse.dto.IngredientStockDTO;
import lk.ijse.vishmibakehouse.dto.LeftoverBalanceDTO;
import lk.ijse.vishmibakehouse.dto.ProductAvailabilityDTO;

import java.sql.SQLException;
import java.util.List;

public interface QueryDAO extends SuperDAO {
    List<IngredientStockDTO> getIngredientStockList() throws SQLException;
    List<IngredientStockDTO> getLowStockAlerts() throws SQLException;
    List<ProductAvailabilityDTO> getProductAvailabilityList() throws SQLException;
    List<LeftoverBalanceDTO> getLeftoverBalances() throws SQLException;
    List<DeliverySummaryDTO> getDeliverySummaries() throws SQLException;
    DashboardSummaryDTO getDashboardSummary() throws SQLException;
}
