package lk.ijse.vishmibakehouse.dao.custom;

import lk.ijse.vishmibakehouse.dao.CrudDAO;
import lk.ijse.vishmibakehouse.entity.LeftoverBatch;

import java.sql.SQLException;
import java.util.List;

public interface LeftoverBatchDAO extends CrudDAO<LeftoverBatch, Long> {
    List<LeftoverBatch> findByProductionBatchId(long productionBatchId) throws SQLException;
    long saveAndReturnId(LeftoverBatch leftover) throws SQLException;
}
