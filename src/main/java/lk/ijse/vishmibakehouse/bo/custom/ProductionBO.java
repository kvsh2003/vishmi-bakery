package lk.ijse.vishmibakehouse.bo.custom;

import lk.ijse.vishmibakehouse.bo.SuperBO;
import lk.ijse.vishmibakehouse.dto.IngredientUsageDTO;
import lk.ijse.vishmibakehouse.dto.ProductionBatchDTO;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ProductionBO extends SuperBO {
    long createProductionBatch(ProductionBatchDTO batchDTO) throws SQLException;
    boolean updateProductionBatch(ProductionBatchDTO batchDTO) throws SQLException;
    boolean updateBatchStatus(long batchId, String status, java.math.BigDecimal actualQty) throws SQLException;
    Optional<ProductionBatchDTO> findBatchById(long batchId) throws SQLException;
    List<ProductionBatchDTO> getAllBatches() throws SQLException;
    List<ProductionBatchDTO> getBatchesByDate(Date date) throws SQLException;

    boolean recordIngredientUsage(IngredientUsageDTO usageDTO) throws SQLException;
    List<IngredientUsageDTO> getUsageByBatch(long batchId) throws SQLException;
    List<IngredientUsageDTO> getAllUsages() throws SQLException;
}
