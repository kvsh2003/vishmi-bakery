package lk.ijse.vishmibakehouse.bo.custom.impl;

import lk.ijse.vishmibakehouse.bo.custom.ProductionBO;
import lk.ijse.vishmibakehouse.dao.DAOFactory;
import lk.ijse.vishmibakehouse.dao.DAOTypes;
import lk.ijse.vishmibakehouse.dao.custom.*;
import lk.ijse.vishmibakehouse.dto.IngredientUsageDTO;
import lk.ijse.vishmibakehouse.dto.ProductionBatchDTO;
import lk.ijse.vishmibakehouse.entity.IngredientUsage;
import lk.ijse.vishmibakehouse.entity.ProductionBatch;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductionBOImpl implements ProductionBO {
    private final ProductionBatchDAO productionBatchDAO = DAOFactory.getInstance().getDAO(DAOTypes.PRODUCTION_BATCH);
    private final IngredientUsageDAO ingredientUsageDAO = DAOFactory.getInstance().getDAO(DAOTypes.INGREDIENT_USAGE);
    private final ProductDAO productDAO = DAOFactory.getInstance().getDAO(DAOTypes.PRODUCT);
    private final EmployeeDAO employeeDAO = DAOFactory.getInstance().getDAO(DAOTypes.EMPLOYEE);
    private final IngredientDAO ingredientDAO = DAOFactory.getInstance().getDAO(DAOTypes.INGREDIENT);

    @Override
    public long createProductionBatch(ProductionBatchDTO batchDTO) throws SQLException {
        return productionBatchDAO.saveAndReturnId(new ProductionBatch(
                0,
                batchDTO.getProductId(),
                batchDTO.getRecordedByEmployeeId(),
                batchDTO.getProducedOn(),
                batchDTO.getPlannedQty(),
                batchDTO.getActualQty() != null ? batchDTO.getActualQty() : BigDecimal.ZERO,
                batchDTO.getStatus() != null ? batchDTO.getStatus() : "PLANNED"
        ));
    }

    @Override
    public boolean updateProductionBatch(ProductionBatchDTO batchDTO) throws SQLException {
        return productionBatchDAO.update(new ProductionBatch(
                batchDTO.getProductionBatchId(),
                batchDTO.getProductId(),
                batchDTO.getRecordedByEmployeeId(),
                batchDTO.getProducedOn(),
                batchDTO.getPlannedQty(),
                batchDTO.getActualQty(),
                batchDTO.getStatus()
        ));
    }

    @Override
    public boolean updateBatchStatus(long batchId, String status, BigDecimal actualQty) throws SQLException {
        Optional<ProductionBatch> opt = productionBatchDAO.findById(batchId);
        if (opt.isPresent()) {
            ProductionBatch pb = opt.get();
            pb.setStatus(status);
            if (actualQty != null) {
                pb.setActualQty(actualQty);
            }
            return productionBatchDAO.update(pb);
        }
        return false;
    }

    @Override
    public Optional<ProductionBatchDTO> findBatchById(long batchId) throws SQLException {
        Optional<ProductionBatch> opt = productionBatchDAO.findById(batchId);
        if (opt.isPresent()) {
            return Optional.of(mapToDTO(opt.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<ProductionBatchDTO> getAllBatches() throws SQLException {
        List<ProductionBatch> list = productionBatchDAO.findAll();
        List<ProductionBatchDTO> dtos = new ArrayList<>();
        for (ProductionBatch pb : list) {
            dtos.add(mapToDTO(pb));
        }
        return dtos;
    }

    @Override
    public List<ProductionBatchDTO> getBatchesByDate(Date date) throws SQLException {
        List<ProductionBatch> list = productionBatchDAO.findByDate(date);
        List<ProductionBatchDTO> dtos = new ArrayList<>();
        for (ProductionBatch pb : list) {
            dtos.add(mapToDTO(pb));
        }
        return dtos;
    }

    @Override
    public boolean recordIngredientUsage(IngredientUsageDTO usageDTO) throws SQLException {
        return ingredientUsageDAO.save(new IngredientUsage(
                0,
                usageDTO.getProductionBatchId(),
                usageDTO.getIngredientId(),
                usageDTO.getRecordedByEmployeeId(),
                usageDTO.getUsedAt(),
                usageDTO.getQty()
        ));
    }

    @Override
    public List<IngredientUsageDTO> getUsageByBatch(long batchId) throws SQLException {
        List<IngredientUsage> list = ingredientUsageDAO.findByProductionBatchId(batchId);
        List<IngredientUsageDTO> dtos = new ArrayList<>();
        for (IngredientUsage iu : list) {
            dtos.add(mapUsageToDTO(iu));
        }
        return dtos;
    }

    @Override
    public List<IngredientUsageDTO> getAllUsages() throws SQLException {
        List<IngredientUsage> list = ingredientUsageDAO.findAll();
        List<IngredientUsageDTO> dtos = new ArrayList<>();
        for (IngredientUsage iu : list) {
            dtos.add(mapUsageToDTO(iu));
        }
        return dtos;
    }

    private ProductionBatchDTO mapToDTO(ProductionBatch pb) throws SQLException {
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
        return dto;
    }

    private IngredientUsageDTO mapUsageToDTO(IngredientUsage iu) throws SQLException {
        IngredientUsageDTO dto = new IngredientUsageDTO(
                iu.getIngredientUsageId(),
                iu.getProductionBatchId(),
                "",
                iu.getIngredientId(),
                "",
                iu.getRecordedByEmployeeId(),
                "",
                iu.getUsedAt(),
                iu.getQty()
        );
        productionBatchDAO.findById(iu.getProductionBatchId()).ifPresent(pb -> {
            try {
                productDAO.findById(pb.getProductId()).ifPresent(p -> dto.setProductName(p.getName()));
            } catch (SQLException ignored) {}
        });
        ingredientDAO.findById(iu.getIngredientId()).ifPresent(i -> dto.setIngredientName(i.getName()));
        employeeDAO.findById(iu.getRecordedByEmployeeId()).ifPresent(e -> dto.setEmployeeName(e.getName()));
        return dto;
    }
}
