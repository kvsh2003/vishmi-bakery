package lk.ijse.vishmibakehouse.bo.custom.impl;

import lk.ijse.vishmibakehouse.bo.custom.DashboardBO;
import lk.ijse.vishmibakehouse.dao.DAOFactory;
import lk.ijse.vishmibakehouse.dao.DAOTypes;
import lk.ijse.vishmibakehouse.dao.custom.EmployeeDAO;
import lk.ijse.vishmibakehouse.dao.custom.ProductDAO;
import lk.ijse.vishmibakehouse.dao.custom.ProductionBatchDAO;
import lk.ijse.vishmibakehouse.dao.custom.QueryDAO;
import lk.ijse.vishmibakehouse.dto.DashboardSummaryDTO;
import lk.ijse.vishmibakehouse.dto.IngredientStockDTO;
import lk.ijse.vishmibakehouse.dto.ProductionBatchDTO;
import lk.ijse.vishmibakehouse.entity.ProductionBatch;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DashboardBOImpl implements DashboardBO {
    private final QueryDAO queryDAO = DAOFactory.getInstance().getDAO(DAOTypes.QUERY);
    private final ProductionBatchDAO productionBatchDAO = DAOFactory.getInstance().getDAO(DAOTypes.PRODUCTION_BATCH);
    private final ProductDAO productDAO = DAOFactory.getInstance().getDAO(DAOTypes.PRODUCT);
    private final EmployeeDAO employeeDAO = DAOFactory.getInstance().getDAO(DAOTypes.EMPLOYEE);

    @Override
    public DashboardSummaryDTO getDashboardSummary() throws SQLException {
        return queryDAO.getDashboardSummary();
    }

    @Override
    public List<ProductionBatchDTO> getTodayProduction() throws SQLException {
        Date today = Date.valueOf(LocalDate.now());
        List<ProductionBatch> list = productionBatchDAO.findByDate(today);
        if (list.isEmpty()) {
            list = productionBatchDAO.findAll(); // Show latest production if no batches yet today
            if (list.size() > 5) {
                list = list.subList(0, 5);
            }
        }
        List<ProductionBatchDTO> dtos = new ArrayList<>();
        for (ProductionBatch pb : list) {
            ProductionBatchDTO dto = new ProductionBatchDTO(
                    pb.getProductionBatchId(),
                    pb.getProductId(),
                    "",
                    pb.getRecordedByEmployeeId(),
                    "",
                    pb.getProducedOn(),
                    pb.getPlannedQty(),
                    pb.getActualQty(),
                    pb.getStatus()
            );
            productDAO.findById(pb.getProductId()).ifPresent(p -> dto.setProductName(p.getName()));
            employeeDAO.findById(pb.getRecordedByEmployeeId()).ifPresent(e -> dto.setEmployeeName(e.getName()));
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public List<IngredientStockDTO> getLowStockAlerts() throws SQLException {
        return queryDAO.getLowStockAlerts();
    }
}
