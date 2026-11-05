package lk.ijse.vishmibakehouse.dao.custom;

import lk.ijse.vishmibakehouse.dao.CrudDAO;
import lk.ijse.vishmibakehouse.entity.Vehicle;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface VehicleDAO extends CrudDAO<Vehicle, Long> {
    Optional<Vehicle> findByRegistrationNo(String registrationNo) throws SQLException;
    List<Vehicle> findActiveVehicles() throws SQLException;
}
