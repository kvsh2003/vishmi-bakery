package lk.ijse.vishmibakehouse.dao.custom.impl;

import lk.ijse.vishmibakehouse.dao.SQLUtil;
import lk.ijse.vishmibakehouse.dao.custom.QueryDAO;
import lk.ijse.vishmibakehouse.dto.DashboardSummaryDTO;
import lk.ijse.vishmibakehouse.dto.DeliverySummaryDTO;
import lk.ijse.vishmibakehouse.dto.IngredientStockDTO;
import lk.ijse.vishmibakehouse.dto.LeftoverBalanceDTO;
import lk.ijse.vishmibakehouse.dto.ProductAvailabilityDTO;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QueryDAOImpl implements QueryDAO {
    @Override
    public List<IngredientStockDTO> getIngredientStockList() throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM v_ingredient_stock ORDER BY name ASC");
        List<IngredientStockDTO> list = new ArrayList<>();
        while (rs.next()) {
            list.add(new IngredientStockDTO(
                    rs.getLong("ingredient_id"),
                    rs.getString("name"),
                    rs.getString("unit"),
                    rs.getBigDecimal("reorder_level"),
                    rs.getBoolean("active"),
                    rs.getBigDecimal("purchased_qty"),
                    rs.getBigDecimal("used_qty"),
                    rs.getBigDecimal("available_qty"),
                    rs.getBoolean("low_stock")
            ));
        }
        return list;
    }

    @Override
    public List<IngredientStockDTO> getLowStockAlerts() throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM v_ingredient_stock WHERE low_stock=1 AND active=1 ORDER BY name ASC");
        List<IngredientStockDTO> list = new ArrayList<>();
        while (rs.next()) {
            list.add(new IngredientStockDTO(
                    rs.getLong("ingredient_id"),
                    rs.getString("name"),
                    rs.getString("unit"),
                    rs.getBigDecimal("reorder_level"),
                    rs.getBoolean("active"),
                    rs.getBigDecimal("purchased_qty"),
                    rs.getBigDecimal("used_qty"),
                    rs.getBigDecimal("available_qty"),
                    rs.getBoolean("low_stock")
            ));
        }
        return list;
    }

    @Override
    public List<ProductAvailabilityDTO> getProductAvailabilityList() throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM v_product_availability ORDER BY name ASC");
        List<ProductAvailabilityDTO> list = new ArrayList<>();
        while (rs.next()) {
            list.add(new ProductAvailabilityDTO(
                    rs.getLong("product_id"),
                    rs.getString("name"),
                    rs.getString("unit"),
                    rs.getBoolean("active"),
                    rs.getBigDecimal("produced_qty"),
                    rs.getBigDecimal("outlet_sold_qty"),
                    rs.getBigDecimal("shop_allocated_qty"),
                    rs.getBigDecimal("leftover_qty"),
                    rs.getBigDecimal("available_qty")
            ));
        }
        return list;
    }

    @Override
    public List<LeftoverBalanceDTO> getLeftoverBalances() throws SQLException {
        ResultSet rs = SQLUtil.executeQuery(
                "SELECT l.*, p.name AS product_name " +
                "FROM v_leftover_balance l " +
                "JOIN leftover_batch lb ON lb.leftover_batch_id=l.leftover_batch_id " +
                "JOIN production_batch pb ON pb.production_batch_id=lb.production_batch_id " +
                "JOIN product p ON p.product_id=pb.product_id " +
                "ORDER BY l.recorded_on DESC, l.leftover_batch_id DESC"
        );
        List<LeftoverBalanceDTO> list = new ArrayList<>();
        while (rs.next()) {
            list.add(new LeftoverBalanceDTO(
                    rs.getLong("leftover_batch_id"),
                    rs.getLong("production_batch_id"),
                    rs.getDate("recorded_on"),
                    rs.getInt("qty_pieces"),
                    rs.getInt("used_pieces"),
                    rs.getInt("remaining_pieces")
            ));
        }
        return list;
    }

    @Override
    public List<DeliverySummaryDTO> getDeliverySummaries() throws SQLException {
        ResultSet rs = SQLUtil.executeQuery("SELECT * FROM v_delivery_summary ORDER BY delivery_date DESC, delivery_id DESC");
        List<DeliverySummaryDTO> list = new ArrayList<>();
        while (rs.next()) {
            list.add(new DeliverySummaryDTO(
                    rs.getLong("delivery_id"),
                    rs.getString("shop_name"),
                    rs.getString("registration_no"),
                    rs.getString("delivery_staff"),
                    rs.getDate("delivery_date"),
                    rs.getString("status"),
                    rs.getBigDecimal("packed_qty"),
                    rs.getBigDecimal("delivered_qty")
            ));
        }
        return list;
    }

    @Override
    public DashboardSummaryDTO getDashboardSummary() throws SQLException {
        DashboardSummaryDTO dto = new DashboardSummaryDTO();

        // 1. Today sales
        ResultSet rsSales = SQLUtil.executeQuery("SELECT COALESCE(SUM(total_sales), 0) AS today_total FROM v_daily_sales WHERE sale_date = CURDATE()");
        if (rsSales.next()) {
            dto.setTodaySales(rsSales.getBigDecimal("today_total"));
        } else {
            dto.setTodaySales(BigDecimal.ZERO);
        }

        // If today has no sales, fallback to latest sales date so demo dashboard is not empty
        if (dto.getTodaySales().compareTo(BigDecimal.ZERO) == 0) {
            ResultSet rsLatestSales = SQLUtil.executeQuery("SELECT COALESCE(SUM(total_sales), 0) AS latest_total FROM v_daily_sales ORDER BY sale_date DESC LIMIT 1");
            if (rsLatestSales.next()) {
                dto.setTodaySales(rsLatestSales.getBigDecimal("latest_total"));
            }
        }

        // 2. Units produced today
        ResultSet rsProd = SQLUtil.executeQuery("SELECT COALESCE(SUM(actual_qty), 0) AS total_units FROM production_batch WHERE status='COMPLETED'");
        if (rsProd.next()) {
            dto.setUnitsProducedToday(rsProd.getInt("total_units"));
        }

        // 3. Pending deliveries
        ResultSet rsDel = SQLUtil.executeQuery("SELECT COUNT(*) AS pending_count FROM delivery WHERE status IN ('PACKED', 'DISPATCHED')");
        if (rsDel.next()) {
            dto.setPendingDeliveries(rsDel.getInt("pending_count"));
        }

        // 4. Low stock ingredients count
        ResultSet rsStock = SQLUtil.executeQuery("SELECT COUNT(*) AS low_count FROM v_ingredient_stock WHERE low_stock=1 AND active=1");
        if (rsStock.next()) {
            dto.setLowStockIngredients(rsStock.getInt("low_count"));
        }

        // 5. Pending and confirmed special orders count
        ResultSet rsSpec = SQLUtil.executeQuery("SELECT COUNT(*) AS total_special FROM special_order WHERE status IN ('PENDING', 'CONFIRMED')");
        if (rsSpec.next()) {
            dto.setSpecialOrders(rsSpec.getInt("total_special"));
        }

        return dto;
    }
}
