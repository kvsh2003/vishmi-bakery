package lk.ijse.vishmibakehouse.dao.custom;

import lk.ijse.vishmibakehouse.dao.CrudDAO;
import lk.ijse.vishmibakehouse.entity.SpecialOrderItem;

import java.sql.SQLException;
import java.util.List;

public interface SpecialOrderItemDAO extends CrudDAO<SpecialOrderItem, Long> {
    List<SpecialOrderItem> findBySpecialOrderId(long specialOrderId) throws SQLException;
    boolean saveAll(List<SpecialOrderItem> items) throws SQLException;
}
