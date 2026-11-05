package lk.ijse.vishmibakehouse.dao.custom;

import lk.ijse.vishmibakehouse.dao.CrudDAO;
import lk.ijse.vishmibakehouse.entity.DeliveryItem;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface DeliveryItemDAO extends CrudDAO<DeliveryItem, Long> {
    List<DeliveryItem> findByDeliveryId(long deliveryId) throws SQLException;
    boolean saveAll(List<DeliveryItem> items) throws SQLException;
    boolean updateDeliveredQty(long deliveryItemId, BigDecimal deliveredQty) throws SQLException;
}
