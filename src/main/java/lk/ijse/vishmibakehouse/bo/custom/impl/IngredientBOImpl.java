package lk.ijse.vishmibakehouse.bo.custom.impl;

import lk.ijse.vishmibakehouse.bo.custom.IngredientBO;
import lk.ijse.vishmibakehouse.dao.DAOFactory;
import lk.ijse.vishmibakehouse.dao.DAOTypes;
import lk.ijse.vishmibakehouse.dao.custom.EmployeeDAO;
import lk.ijse.vishmibakehouse.dao.custom.IngredientDAO;
import lk.ijse.vishmibakehouse.dao.custom.IngredientPurchaseDAO;
import lk.ijse.vishmibakehouse.dao.custom.QueryDAO;
import lk.ijse.vishmibakehouse.dto.IngredientDTO;
import lk.ijse.vishmibakehouse.dto.IngredientPurchaseDTO;
import lk.ijse.vishmibakehouse.dto.IngredientStockDTO;
import lk.ijse.vishmibakehouse.entity.Ingredient;
import lk.ijse.vishmibakehouse.entity.IngredientPurchase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IngredientBOImpl implements IngredientBO {
    private final IngredientDAO ingredientDAO = DAOFactory.getInstance().getDAO(DAOTypes.INGREDIENT);
    private final IngredientPurchaseDAO purchaseDAO = DAOFactory.getInstance().getDAO(DAOTypes.INGREDIENT_PURCHASE);
    private final EmployeeDAO employeeDAO = DAOFactory.getInstance().getDAO(DAOTypes.EMPLOYEE);
    private final QueryDAO queryDAO = DAOFactory.getInstance().getDAO(DAOTypes.QUERY);

    @Override
    public boolean saveIngredient(IngredientDTO dto) throws SQLException {
        return ingredientDAO.save(new Ingredient(
                0, dto.getName(), dto.getUnit(), dto.getReorderLevel(), dto.isActive()
        ));
    }

    @Override
    public boolean updateIngredient(IngredientDTO dto) throws SQLException {
        return ingredientDAO.update(new Ingredient(
                dto.getIngredientId(), dto.getName(), dto.getUnit(), dto.getReorderLevel(), dto.isActive()
        ));
    }

    @Override
    public boolean deleteIngredient(long ingredientId) throws SQLException {
        return ingredientDAO.delete(ingredientId);
    }

    @Override
    public Optional<IngredientDTO> findIngredientById(long ingredientId) throws SQLException {
        return ingredientDAO.findById(ingredientId).map(i -> new IngredientDTO(
                i.getIngredientId(), i.getName(), i.getUnit(), i.getReorderLevel(), i.isActive()
        ));
    }

    @Override
    public List<IngredientDTO> getAllIngredients() throws SQLException {
        List<Ingredient> list = ingredientDAO.findAll();
        List<IngredientDTO> dtos = new ArrayList<>();
        for (Ingredient i : list) {
            dtos.add(new IngredientDTO(
                    i.getIngredientId(), i.getName(), i.getUnit(), i.getReorderLevel(), i.isActive()
            ));
        }
        return dtos;
    }

    @Override
    public List<IngredientDTO> getActiveIngredients() throws SQLException {
        List<Ingredient> list = ingredientDAO.findActiveIngredients();
        List<IngredientDTO> dtos = new ArrayList<>();
        for (Ingredient i : list) {
            dtos.add(new IngredientDTO(
                    i.getIngredientId(), i.getName(), i.getUnit(), i.getReorderLevel(), i.isActive()
            ));
        }
        return dtos;
    }

    @Override
    public boolean recordPurchase(IngredientPurchaseDTO purchaseDTO) throws SQLException {
        return purchaseDAO.save(new IngredientPurchase(
                0,
                purchaseDTO.getIngredientId(),
                purchaseDTO.getRecordedByEmployeeId(),
                purchaseDTO.getPurchasedAt(),
                purchaseDTO.getQty(),
                purchaseDTO.getUnitCost(),
                purchaseDTO.getSupplierName()
        ));
    }

    @Override
    public List<IngredientPurchaseDTO> getAllPurchases() throws SQLException {
        List<IngredientPurchase> list = purchaseDAO.findAll();
        List<IngredientPurchaseDTO> dtos = new ArrayList<>();
        for (IngredientPurchase ip : list) {
            IngredientPurchaseDTO dto = new IngredientPurchaseDTO(
                    ip.getIngredientPurchaseId(),
                    ip.getIngredientId(),
                    "",
                    ip.getRecordedByEmployeeId(),
                    "",
                    ip.getPurchasedAt(),
                    ip.getQty(),
                    ip.getUnitCost(),
                    ip.getSupplierName(),
                    ip.getTotalCost()
            );
            ingredientDAO.findById(ip.getIngredientId()).ifPresent(i -> dto.setIngredientName(i.getName()));
            employeeDAO.findById(ip.getRecordedByEmployeeId()).ifPresent(e -> dto.setEmployeeName(e.getName()));
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public List<IngredientStockDTO> getIngredientStockList() throws SQLException {
        return queryDAO.getIngredientStockList();
    }

    @Override
    public List<IngredientStockDTO> getLowStockAlerts() throws SQLException {
        return queryDAO.getLowStockAlerts();
    }
}
