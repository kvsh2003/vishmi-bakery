package lk.ijse.vishmibakehouse.dao.custom;

import lk.ijse.vishmibakehouse.dao.CrudDAO;
import lk.ijse.vishmibakehouse.entity.SaleItem;

import java.sql.SQLException;
import java.util.List;

public interface SaleItemDAO extends CrudDAO<SaleItem, Long> {
    List<SaleItem> findBySaleId(long saleId) throws SQLException;
    boolean saveAll(List<SaleItem> items) throws SQLException;
}
