package lk.ijse.vishmibakehouse.dao.custom;

import lk.ijse.vishmibakehouse.dao.CrudDAO;
import lk.ijse.vishmibakehouse.entity.Sale;

import java.sql.SQLException;

public interface SaleDAO extends CrudDAO<Sale, Long> {
    long saveAndReturnId(Sale sale) throws SQLException;
}
