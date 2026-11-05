package lk.ijse.vishmibakehouse.dao.custom;

import lk.ijse.vishmibakehouse.dao.CrudDAO;
import lk.ijse.vishmibakehouse.entity.ProductionBatch;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public interface ProductionBatchDAO extends CrudDAO<ProductionBatch, Long> {
    List<ProductionBatch> findByDate(Date date) throws SQLException;
    List<ProductionBatch> findByStatus(String status) throws SQLException;
    long saveAndReturnId(ProductionBatch batch) throws SQLException;
}
