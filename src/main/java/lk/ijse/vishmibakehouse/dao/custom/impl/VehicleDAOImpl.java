package lk.ijse.vishmibakehouse.dao.custom.impl;

import lk.ijse.vishmibakehouse.dao.SQLUtil;
import lk.ijse.vishmibakehouse.dao.custom.VehicleDAO;
import lk.ijse.vishmibakehouse.entity.Vehicle;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VehicleDAOImpl implements VehicleDAO {
    @Override
    public boolean save(Vehicle entity) throws SQLException {
        return SQLUtil.executeUpdate(
                "INSERT INTO vehicle (registration_no, model, active) VALUES (?, ?, ?)",
                entity.getRegistrationNo(), entity.getModel(), entity.isActive() ? 1 : 0
        );
    }

    @Override
    public boolean update(Vehicle entity) throws SQLException {
        return SQLUtil.executeUpdate(
                "UPDATE vehicle SET registration_no=?, model=?, active=? WHERE vehicle_id=?",
                entity.getRegistrationNo(), entity.getModel(), entity.isActive() ? 1 : 0, entity.getVehicleId()
        );
    }

    @Override
    public boolean delete(Long id) throws SQLException {
        return SQLUtil.executeUpdate("UPDATE vehicle SET active=0 WHERE vehicle_id=?", id);
    }

    @Override
    public Optional<Vehicle> findById(Long id) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM vehicle WHERE vehicle_id=?", id);
        if (rs.next()) {
            return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    @Override
    public List<Vehicle> findAll() throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM vehicle ORDER BY vehicle_id ASC");
        List<Vehicle> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public Optional<Vehicle> findByRegistrationNo(String registrationNo) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM vehicle WHERE registration_no=?", registrationNo);
        if (rs.next()) {
            return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    @Override
    public List<Vehicle> findActiveVehicles() throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM vehicle WHERE active=1 ORDER BY registration_no ASC");
        List<Vehicle> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    private Vehicle mapRow(ResultSet rs) throws SQLException {
        return new Vehicle(
                rs.getLong("vehicle_id"),
                rs.getString("registration_no"),
                rs.getString("model"),
                rs.getBoolean("active")
        );
    }
}
