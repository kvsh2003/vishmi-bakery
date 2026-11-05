package lk.ijse.vishmibakehouse.dao.custom;

import lk.ijse.vishmibakehouse.dao.CrudDAO;
import lk.ijse.vishmibakehouse.entity.SpecialOrder;

import java.sql.SQLException;
import java.util.List;

public interface SpecialOrderDAO extends CrudDAO<SpecialOrder, Long> {
    long saveAndReturnId(SpecialOrder order) throws SQLException;
    List<SpecialOrder> findByCustomerId(long customerId) throws SQLException;
    List<SpecialOrder> findByStatus(String status) throws SQLException;
    boolean updateStatus(long specialOrderId, String status) throws SQLException;
}
