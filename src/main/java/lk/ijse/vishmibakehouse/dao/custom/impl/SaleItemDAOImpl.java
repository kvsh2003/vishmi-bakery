package lk.ijse.vishmibakehouse.dao.custom.impl;

import lk.ijse.vishmibakehouse.dao.SQLUtil;
import lk.ijse.vishmibakehouse.dao.custom.SaleItemDAO;
import lk.ijse.vishmibakehouse.entity.SaleItem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SaleItemDAOImpl implements SaleItemDAO {
    @Override
    public boolean save(SaleItem entity) throws SQLException {
        return SQLUtil.executeUpdate(
                "INSERT INTO sale_item (sale_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)",
                entity.getSaleId(), entity.getProductId(), entity.getQuantity(), entity.getUnitPrice()
        );
    }

    @Override
    public boolean saveAll(List<SaleItem> items) throws SQLException {
        for (SaleItem item : items) {
            if (!save(item)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean update(SaleItem entity) throws SQLException {
        return SQLUtil.executeUpdate(
                "UPDATE sale_item SET sale_id=?, product_id=?, quantity=?, unit_price=? WHERE sale_item_id=?",
                entity.getSaleId(), entity.getProductId(), entity.getQuantity(), entity.getUnitPrice(), entity.getSaleItemId()
        );
    }

    @Override
    public boolean delete(Long id) throws SQLException {
        return SQLUtil.executeUpdate("DELETE FROM sale_item WHERE sale_item_id=?", id);
    }

    @Override
    public Optional<SaleItem> findById(Long id) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM sale_item WHERE sale_item_id=?", id);
        if (rs.next()) {
            return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    @Override
    public List<SaleItem> findAll() throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM sale_item");
        List<SaleItem> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<SaleItem> findBySaleId(long saleId) throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM sale_item WHERE sale_id=? ORDER BY sale_item_id ASC", saleId);
        List<SaleItem> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    private SaleItem mapRow(ResultSet rs) throws SQLException {
        SaleItem si = new SaleItem(
                rs.getLong("sale_item_id"),
                rs.getLong("sale_id"),
                rs.getLong("product_id"),
                rs.getBigDecimal("quantity"),
                rs.getBigDecimal("unit_price")
        );
        si.setLineTotal(rs.getBigDecimal("line_total"));
        return si;
    }
}
