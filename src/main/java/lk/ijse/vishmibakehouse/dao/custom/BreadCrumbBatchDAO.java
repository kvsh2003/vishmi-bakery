package lk.ijse.vishmibakehouse.dao.custom;

import lk.ijse.vishmibakehouse.dao.CrudDAO;
import lk.ijse.vishmibakehouse.entity.BreadCrumbBatch;
import lk.ijse.vishmibakehouse.entity.BreadCrumbInput;

import java.sql.SQLException;
import java.util.List;

public interface BreadCrumbBatchDAO extends CrudDAO<BreadCrumbBatch, Long> {
    long saveAndReturnId(BreadCrumbBatch batch) throws SQLException;
    boolean saveBreadCrumbInput(BreadCrumbInput input) throws SQLException;
    List<BreadCrumbInput> findInputsByBatchId(long breadCrumbBatchId) throws SQLException;
}
