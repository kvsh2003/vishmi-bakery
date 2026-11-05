package lk.ijse.vishmibakehouse.bo.custom;

import lk.ijse.vishmibakehouse.bo.SuperBO;
import lk.ijse.vishmibakehouse.dto.SaleDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface SaleBO extends SuperBO {
    boolean placeSale(SaleDTO saleDTO) throws SQLException;
    Optional<SaleDTO> findSaleById(long saleId) throws SQLException;
    List<SaleDTO> getAllSales() throws SQLException;
}
