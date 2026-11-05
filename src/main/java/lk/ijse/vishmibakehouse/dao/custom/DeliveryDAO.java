package lk.ijse.vishmibakehouse.dao.custom;

import lk.ijse.vishmibakehouse.dao.CrudDAO;
import lk.ijse.vishmibakehouse.entity.Delivery;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public interface DeliveryDAO extends CrudDAO<Delivery, Long> {
    long saveAndReturnId(Delivery delivery) throws SQLException;
    List<Delivery> findByDate(Date date) throws SQLException;
    List<Delivery> findByStatus(String status) throws SQLException;
    boolean updateStatusAndVehicle(long deliveryId, String status, Long vehicleId, Long deliveryEmployeeId) throws SQLException;
}
