package lk.ijse.vishmibakehouse.bo.custom.impl;

import lk.ijse.vishmibakehouse.bo.custom.SaleBO;
import lk.ijse.vishmibakehouse.dao.DAOFactory;
import lk.ijse.vishmibakehouse.dao.DAOTypes;
import lk.ijse.vishmibakehouse.dao.custom.EmployeeDAO;
import lk.ijse.vishmibakehouse.dao.custom.ProductDAO;
import lk.ijse.vishmibakehouse.dao.custom.SaleDAO;
import lk.ijse.vishmibakehouse.dao.custom.SaleItemDAO;
import lk.ijse.vishmibakehouse.db.DBConnection;
import lk.ijse.vishmibakehouse.dto.SaleDTO;
import lk.ijse.vishmibakehouse.dto.SaleItemDTO;
import lk.ijse.vishmibakehouse.entity.Sale;
import lk.ijse.vishmibakehouse.entity.SaleItem;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SaleBOImpl implements SaleBO {
    private final SaleDAO saleDAO = DAOFactory.getInstance().getDAO(DAOTypes.SALE);
    private final SaleItemDAO saleItemDAO = DAOFactory.getInstance().getDAO(DAOTypes.SALE_ITEM);
    private final ProductDAO productDAO = DAOFactory.getInstance().getDAO(DAOTypes.PRODUCT);
    private final EmployeeDAO employeeDAO = DAOFactory.getInstance().getDAO(DAOTypes.EMPLOYEE);

    @Override
    public boolean placeSale(SaleDTO saleDTO) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        boolean originalAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);

            Sale sale = new Sale(
                    0,
                    saleDTO.getCashierEmployeeId(),
                    saleDTO.getSoldAt(),
                    saleDTO.getCashReceived()
            );

            long saleId = saleDAO.saveAndReturnId(sale);
            if (saleId <= 0) {
                connection.rollback();
                return false;
            }
            saleDTO.setSaleId(saleId);

            for (SaleItemDTO itemDTO : saleDTO.getItems()) {
                SaleItem item = new SaleItem(
                        0,
                        saleId,
                        itemDTO.getProductId(),
                        itemDTO.getQuantity(),
                        itemDTO.getUnitPrice()
                );
                if (!saleItemDAO.save(item)) {
                    connection.rollback();
                    return false;
                }
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
    }

    @Override
    public Optional<SaleDTO> findSaleById(long saleId) throws SQLException {
        Optional<Sale> opt = saleDAO.findById(saleId);
        if (opt.isPresent()) {
            Sale s = opt.get();
            SaleDTO dto = new SaleDTO(
                    s.getSaleId(),
                    s.getCashierEmployeeId(),
                    "",
                    s.getSoldAt(),
                    s.getCashReceived(),
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            );
            employeeDAO.findById(s.getCashierEmployeeId()).ifPresent(e -> dto.setCashierName(e.getName()));

            List<SaleItem> items = saleItemDAO.findBySaleId(saleId);
            BigDecimal total = BigDecimal.ZERO;
            List<SaleItemDTO> itemDTOs = new ArrayList<>();
            for (SaleItem si : items) {
                SaleItemDTO itemDTO = new SaleItemDTO(
                        si.getSaleItemId(),
                        si.getSaleId(),
                        si.getProductId(),
                        "",
                        si.getQuantity(),
                        si.getUnitPrice(),
                        si.getLineTotal()
                );
                productDAO.findById(si.getProductId()).ifPresent(p -> itemDTO.setProductName(p.getName()));
                itemDTOs.add(itemDTO);
                total = total.add(si.getLineTotal());
            }
            dto.setItems(itemDTOs);
            dto.setTotalAmount(total);
            dto.setChangeAmount(dto.getCashReceived().subtract(total));

            return Optional.of(dto);
        }
        return Optional.empty();
    }

    @Override
    public List<SaleDTO> getAllSales() throws SQLException {
        List<Sale> list = saleDAO.findAll();
        List<SaleDTO> dtos = new ArrayList<>();
        for (Sale s : list) {
            SaleDTO dto = new SaleDTO(
                    s.getSaleId(),
                    s.getCashierEmployeeId(),
                    "",
                    s.getSoldAt(),
                    s.getCashReceived(),
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            );
            employeeDAO.findById(s.getCashierEmployeeId()).ifPresent(e -> dto.setCashierName(e.getName()));

            List<SaleItem> items = saleItemDAO.findBySaleId(s.getSaleId());
            BigDecimal total = BigDecimal.ZERO;
            for (SaleItem si : items) {
                total = total.add(si.getLineTotal() != null ? si.getLineTotal() : si.getQuantity().multiply(si.getUnitPrice()));
            }
            dto.setTotalAmount(total);
            dto.setChangeAmount(dto.getCashReceived().subtract(total));
            dtos.add(dto);
        }
        return dtos;
    }
}
