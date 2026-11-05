package lk.ijse.vishmibakehouse.dao.custom;

import lk.ijse.vishmibakehouse.dao.CrudDAO;
import lk.ijse.vishmibakehouse.entity.Shop;

import java.sql.SQLException;
import java.util.List;

public interface ShopDAO extends CrudDAO<Shop, Long> {
    List<Shop> findActiveShops() throws SQLException;
}
