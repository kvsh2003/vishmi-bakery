package lk.ijse.vishmibakehouse.dao.custom.impl;

import lk.ijse.vishmibakehouse.dao.SQLUtil;
import lk.ijse.vishmibakehouse.dao.custom.SaleDAO;
import lk.ijse.vishmibakehouse.entity.Sale;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SaleDAOImpl implements SaleDAO {
    @Override
    public boolean save(Sale entity) throws SQLException {
        return saveAndReturnId(entity) > 0;
    }

    @Override
    public long saveAndReturnId(Sale entity) throws SQLException {
        return SQLUtil.executeInsertReturnGeneratedId(
                "INSERT INTO sale (cashier_employee_id, sold_at, cash_received) VALUES (?, ?, ?)",
                entity.getCashierEmployeeId(), entity.getSoldAt(), entity.getCashReceived()
        );
    }

    @Override
    public boolean update(Sale entity) throws SQLException {
        return SQLUtil.executeUpdate(
                "UPDATE sale SET cashier_employee_id=?, sold_at=?, cash_received=? WHERE sale_id=?",
                entity.getCashierEmployeeId(), entity.getSoldAt(), entity.getCashReceived(), entity.getSaleId()
        );
    }

    @Override
    public boolean delete(Long id) throws SQLException {
        return SQLUtil.executeUpdate("DELETE FROM sale WHERE sale_id=?", id);
    }

    @Override
    public Optional<Sale> findById(Long id) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM sale WHERE sale_id=?", id);
        if (rs.next()) {
            return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    @Override
    public List<Sale> findAll() throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM sale ORDER BY sold_at DESC");
        List<Sale> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    private Sale mapRow(ResultSet rs) throws SQLException {
        Sale s = new Sale(
                rs.getLong("sale_id"),
                rs.getLong("cashier_employee_id"),
                rs.getTimestamp("sold_at"),
                rs.getBigDecimal("cash_received")
        );
        s.setCreatedAt(rs.getTimestamp("created_at"));
        return s;
    }
}
