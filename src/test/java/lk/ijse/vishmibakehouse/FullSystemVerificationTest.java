package lk.ijse.vishmibakehouse;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lk.ijse.vishmibakehouse.bo.BOFactory;
import lk.ijse.vishmibakehouse.bo.BOTypes;
import lk.ijse.vishmibakehouse.bo.custom.*;
import lk.ijse.vishmibakehouse.db.DBConnection;
import lk.ijse.vishmibakehouse.dto.*;
import net.sf.jasperreports.engine.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class FullSystemVerificationTest {

    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
            // JavaFX Platform already initialized
        }
    }

    @Test
    @DisplayName("Verify Database Tables and Views")
    void testDatabaseStructure() throws Exception {
        Connection conn = DBConnection.getInstance().getConnection();
        assertNotNull(conn, "Database connection must not be null");

        DatabaseMetaData meta = conn.getMetaData();
        List<String> requiredTables = List.of(
                "employee", "user_account", "product", "customer", "ingredient",
                "shop", "vehicle", "ingredient_purchase", "production_batch",
                "ingredient_usage", "sale", "sale_item", "special_order",
                "special_order_item", "leftover_batch", "bread_crumb_batch",
                "bread_crumb_input", "delivery", "delivery_item"
        );

        Set<String> existingTables = new HashSet<>();
        try (ResultSet rs = meta.getTables(conn.getCatalog(), null, "%", new String[]{"TABLE", "VIEW"})) {
            while (rs.next()) {
                existingTables.add(rs.getString("TABLE_NAME").toLowerCase());
            }
        }

        for (String table : requiredTables) {
            assertTrue(existingTables.contains(table), "Missing required table: " + table);
        }

        List<String> requiredViews = List.of(
                "v_daily_sales", "v_delivery_summary", "v_ingredient_stock",
                "v_leftover_balance", "v_product_availability",
                "v_sale_totals", "v_special_order_totals"
        );
        for (String view : requiredViews) {
            assertTrue(existingTables.contains(view), "Missing required view: " + view);
        }
    }

    @Test
    @DisplayName("Verify All Business Object (BO) Operations")
    void testAllBusinessObjects() throws Exception {
        // 1. AuthBO
        AuthBO authBO = (AuthBO) BOFactory.getInstance().getBO(BOTypes.AUTH);
        UserAccountDTO user = authBO.authenticate("admin", "admin123").orElse(null);
        assertNotNull(user, "Admin user must authenticate successfully");
        assertEquals("OWNER", user.getAccessRole());

        // 2. DashboardBO
        DashboardBO dashboardBO = (DashboardBO) BOFactory.getInstance().getBO(BOTypes.DASHBOARD);
        DashboardSummaryDTO summary = dashboardBO.getDashboardSummary();
        assertNotNull(summary, "Dashboard summary must be generated");
        assertNotNull(summary.getTodaySales());
        assertTrue(summary.getUnitsProducedToday() >= 0);

        // 3. ProductBO
        ProductBO productBO = (ProductBO) BOFactory.getInstance().getBO(BOTypes.PRODUCT);
        List<ProductDTO> products = productBO.getAllProducts();
        assertFalse(products.isEmpty(), "Products list should not be empty");
        List<ProductAvailabilityDTO> availabilities = productBO.getProductAvailability();
        assertFalse(availabilities.isEmpty(), "Product availability list should not be empty");

        // 4. IngredientBO
        IngredientBO ingredientBO = (IngredientBO) BOFactory.getInstance().getBO(BOTypes.INGREDIENT);
        List<IngredientDTO> ingredients = ingredientBO.getAllIngredients();
        assertFalse(ingredients.isEmpty(), "Ingredients list should not be empty");
        List<IngredientStockDTO> stockList = ingredientBO.getIngredientStockList();
        assertFalse(stockList.isEmpty(), "Stock levels should not be empty");

        // 5. EmployeeBO
        EmployeeBO employeeBO = (EmployeeBO) BOFactory.getInstance().getBO(BOTypes.EMPLOYEE);
        List<EmployeeDTO> employees = employeeBO.getAllEmployees();
        assertFalse(employees.isEmpty(), "Employee list should not be empty");
        List<UserAccountDTO> users = employeeBO.getAllUserAccounts();
        assertFalse(users.isEmpty(), "User accounts list should not be empty");

        // 6. ProductionBO
        ProductionBO productionBO = (ProductionBO) BOFactory.getInstance().getBO(BOTypes.PRODUCTION);
        List<ProductionBatchDTO> batches = productionBO.getAllBatches();
        assertFalse(batches.isEmpty(), "Production batches should not be empty");

        // 7. DeliveryBO
        DeliveryBO deliveryBO = (DeliveryBO) BOFactory.getInstance().getBO(BOTypes.DELIVERY);
        List<DeliveryDTO> deliveries = deliveryBO.getAllDeliveries();
        assertFalse(deliveries.isEmpty(), "Deliveries list should not be empty");
        List<ShopDTO> shops = deliveryBO.getAllShops();
        assertFalse(shops.isEmpty(), "Shops list should not be empty");
        List<VehicleDTO> vehicles = deliveryBO.getAllVehicles();
        assertFalse(vehicles.isEmpty(), "Vehicles list should not be empty");

        // 8. SpecialOrderBO
        SpecialOrderBO specialOrderBO = (SpecialOrderBO) BOFactory.getInstance().getBO(BOTypes.SPECIAL_ORDER);
        List<SpecialOrderDTO> specialOrders = specialOrderBO.getAllOrders();
        assertFalse(specialOrders.isEmpty(), "Special orders list should not be empty");

        // 9. LeftoverBO
        LeftoverBO leftoverBO = (LeftoverBO) BOFactory.getInstance().getBO(BOTypes.LEFTOVER);
        List<LeftoverBatchDTO> leftovers = leftoverBO.getAllLeftovers();
        assertFalse(leftovers.isEmpty(), "Leftover batches list should not be empty");
        List<LeftoverBalanceDTO> balances = leftoverBO.getLeftoverBalances();
        assertFalse(balances.isEmpty(), "Leftover balances list should not be empty");

        // 10. SaleBO
        SaleBO saleBO = (SaleBO) BOFactory.getInstance().getBO(BOTypes.SALE);
        List<SaleDTO> sales = saleBO.getAllSales();
        assertFalse(sales.isEmpty(), "Sales list should not be empty");
    }

    @Test
    @DisplayName("Verify FXML Views Syntax and XML Integrity")
    void testFxmlIntegrity() throws Exception {
        String[] fxmlFiles = {
                "/view/01-login.fxml",
                "/view/main_layout.fxml",
                "/view/02-dashboard.fxml",
                "/view/03-products.fxml",
                "/view/04-production.fxml",
                "/view/05-ingredients.fxml",
                "/view/06-purchases.fxml",
                "/view/07-usage.fxml",
                "/view/08-outlet-sales.fxml",
                "/view/09-packing-dispatch.fxml",
                "/view/10-deliveries.fxml",
                "/view/11-special-orders.fxml",
                "/view/12-leftovers.fxml",
                "/view/13-employees.fxml",
                "/view/14-reports.fxml"
        };

        for (String path : fxmlFiles) {
            URL url = getClass().getResource(path);
            assertNotNull(url, "FXML file missing: " + path);
            assertDoesNotThrow(() -> {
                FXMLLoader loader = new FXMLLoader(url);
                Parent root = loader.load();
                assertNotNull(root, "Loaded root must not be null for: " + path);
            }, "FXML parsing or controller instantiation failed for: " + path);
        }
    }

    @Test
    @DisplayName("Verify Dynamic JasperReports Generation and Fill with DB Data")
    void testJasperReportsWithData() throws Exception {
        Connection conn = DBConnection.getInstance().getConnection();
        String[] reportFiles = {
                "/reports/daily_sales_report.jrxml",
                "/reports/ingredient_stock_report.jrxml",
                "/reports/delivery_summary_report.jrxml",
                "/reports/production_summary_report.jrxml"
        };

        for (String path : reportFiles) {
            try (InputStream in = getClass().getResourceAsStream(path)) {
                assertNotNull(in, "Report file missing: " + path);
                JasperReport jasperReport = JasperCompileManager.compileReport(in);
                assertNotNull(jasperReport, "Report compilation failed for: " + path);

                Map<String, Object> params = new HashMap<>();
                JasperPrint print = JasperFillManager.fillReport(jasperReport, params, conn);
                assertNotNull(print, "JasperPrint must not be null for: " + path);
            }
        }
    }
}
