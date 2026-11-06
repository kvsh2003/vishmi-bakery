package lk.ijse.vishmibakehouse.bo.custom;

import lk.ijse.vishmibakehouse.bo.SuperBO;
import lk.ijse.vishmibakehouse.dto.DashboardSummaryDTO;
import lk.ijse.vishmibakehouse.dto.DeliverySummaryDTO;
import lk.ijse.vishmibakehouse.dto.IngredientStockDTO;
import lk.ijse.vishmibakehouse.dto.ProductAvailabilityDTO;
import lk.ijse.vishmibakehouse.dto.ProductionBatchDTO;

import java.sql.SQLException;
import java.util.List;

public interface DashboardBO extends SuperBO {
    DashboardSummaryDTO getDashboardSummary() throws SQLException;
    List<ProductionBatchDTO> getTodayProduction() throws SQLException;
    List<IngredientStockDTO> getLowStockAlerts() throws SQLException;
    List<ProductAvailabilityDTO> getProductAvailability() throws SQLException;
    List<DeliverySummaryDTO> getRecentDeliveries() throws SQLException;
}
