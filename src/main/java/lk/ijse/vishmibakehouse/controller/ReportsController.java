package lk.ijse.vishmibakehouse.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.vishmibakehouse.dao.SQLUtil;
import lk.ijse.vishmibakehouse.db.DBConnection;
import lk.ijse.vishmibakehouse.util.AlertUtil;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import java.awt.Desktop;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class ReportsController {

    @FXML
    private Label lblPeriodSales;

    @FXML
    private Label lblPeriodItems;

    @FXML
    private Label lblPeriodOrders;

    @FXML
    private Label lblPeriodAvg;

    @FXML
    private Label lblTableTitle;

    @FXML
    private Label lblRecordCount;

    @FXML
    private ComboBox<String> cmbReportType;

    @FXML
    private DatePicker dtFromDate;

    @FXML
    private DatePicker dtToDate;

    @FXML
    private TableView<ItemizedSaleRecord> tblItemizedSales;

    @FXML
    private TableColumn<ItemizedSaleRecord, String> colSaleId;
    @FXML
    private TableColumn<ItemizedSaleRecord, String> colSaleTime;
    @FXML
    private TableColumn<ItemizedSaleRecord, String> colCashier;
    @FXML
    private TableColumn<ItemizedSaleRecord, String> colProduct;
    @FXML
    private TableColumn<ItemizedSaleRecord, String> colCategory;
    @FXML
    private TableColumn<ItemizedSaleRecord, String> colQty;
    @FXML
    private TableColumn<ItemizedSaleRecord, String> colUnitPrice;
    @FXML
    private TableColumn<ItemizedSaleRecord, String> colLineTotal;
    @FXML
    private TableColumn<ItemizedSaleRecord, String> colCashReceived;
    @FXML
    private TableColumn<ItemizedSaleRecord, String> colChange;

    private final DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @FXML
    public void initialize() {
        cmbReportType.setItems(FXCollections.observableArrayList(
                "Itemized Counter Sales & Transaction Audit",
                "Ingredient Inventory & Low Stock Alert",
                "Wholesale Shop Deliveries Summary",
                "Production & Manufacturing Report"
        ));
        cmbReportType.getSelectionModel().selectFirst();

        dtFromDate.setValue(LocalDate.now().minusDays(30));
        dtToDate.setValue(LocalDate.now().plusDays(1));

        setupTableColumns();
        loadItemizedSalesData();

        dtFromDate.valueProperty().addListener((obs, o, n) -> loadItemizedSalesData());
        dtToDate.valueProperty().addListener((obs, o, n) -> loadItemizedSalesData());
    }

    private void setupTableColumns() {
        colSaleId.setCellValueFactory(cellData -> cellData.getValue().saleIdProperty());
        colSaleTime.setCellValueFactory(cellData -> cellData.getValue().soldAtProperty());
        colCashier.setCellValueFactory(cellData -> cellData.getValue().cashierNameProperty());
        colProduct.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
        colCategory.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        colQty.setCellValueFactory(cellData -> cellData.getValue().qtyProperty());
        colUnitPrice.setCellValueFactory(cellData -> cellData.getValue().unitPriceProperty());
        colLineTotal.setCellValueFactory(cellData -> cellData.getValue().lineTotalProperty());
        colCashReceived.setCellValueFactory(cellData -> cellData.getValue().cashReceivedProperty());
        colChange.setCellValueFactory(cellData -> cellData.getValue().changeProperty());

        // Cell styling for line total
        colLineTotal.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #15803D; -fx-font-weight: bold; -fx-alignment: CENTER-RIGHT;");
                }
            }
        });

        colUnitPrice.setStyle("-fx-alignment: CENTER-RIGHT;");
        colQty.setStyle("-fx-alignment: CENTER-RIGHT;");
        colCashReceived.setStyle("-fx-alignment: CENTER-RIGHT;");
        colChange.setStyle("-fx-alignment: CENTER-RIGHT;");
    }

    private void loadItemizedSalesData() {
        LocalDate from = dtFromDate.getValue() != null ? dtFromDate.getValue() : LocalDate.now().minusDays(30);
        LocalDate to = dtToDate.getValue() != null ? dtToDate.getValue() : LocalDate.now().plusDays(1);

        String sql = "SELECT s.sale_id, s.sold_at, e.name AS cashier_name, p.name AS product_name, " +
                "p.category AS category, si.quantity, si.unit_price, si.line_total, " +
                "s.cash_received, (s.cash_received - v.total_amount) AS change_amount " +
                "FROM sale s " +
                "JOIN employee e ON e.employee_id = s.cashier_employee_id " +
                "JOIN sale_item si ON si.sale_id = s.sale_id " +
                "JOIN product p ON p.product_id = si.product_id " +
                "JOIN v_sale_totals v ON v.sale_id = s.sale_id " +
                "WHERE DATE(s.sold_at) BETWEEN ? AND ? " +
                "ORDER BY s.sold_at DESC, s.sale_id DESC, si.sale_item_id ASC";

        try {
            ResultSet rs = SQLUtil.executeQuery(sql, Date.valueOf(from), Date.valueOf(to));
            List<ItemizedSaleRecord> list = new ArrayList<>();

            BigDecimal totalRevenue = BigDecimal.ZERO;
            BigDecimal totalUnits = BigDecimal.ZERO;
            Set<Long> uniqueOrders = new HashSet<>();

            while (rs.next()) {
                long saleId = rs.getLong("sale_id");
                Timestamp soldAt = rs.getTimestamp("sold_at");
                String cashier = rs.getString("cashier_name");
                String product = rs.getString("product_name");
                String category = rs.getString("category");
                BigDecimal qty = rs.getBigDecimal("quantity");
                BigDecimal unitPrice = rs.getBigDecimal("unit_price");
                BigDecimal lineTotal = rs.getBigDecimal("line_total");
                BigDecimal cashReceived = rs.getBigDecimal("cash_received");
                BigDecimal changeAmount = rs.getBigDecimal("change_amount");

                list.add(new ItemizedSaleRecord(
                        "#" + saleId,
                        soldAt != null ? dateTimeFormat.format(soldAt) : "",
                        cashier,
                        product,
                        category != null ? category : "General",
                        qty != null ? qty.stripTrailingZeros().toPlainString() : "0",
                        currencyFormat.format(unitPrice),
                        currencyFormat.format(lineTotal),
                        currencyFormat.format(cashReceived),
                        changeAmount != null ? currencyFormat.format(changeAmount) : "0.00"
                ));

                if (lineTotal != null) totalRevenue = totalRevenue.add(lineTotal);
                if (qty != null) totalUnits = totalUnits.add(qty);
                uniqueOrders.add(saleId);
            }

            tblItemizedSales.setItems(FXCollections.observableArrayList(list));

            // Update KPI metric summary cards
            lblPeriodSales.setText("LKR " + currencyFormat.format(totalRevenue));
            lblPeriodItems.setText(totalUnits.stripTrailingZeros().toPlainString() + " units");
            lblPeriodOrders.setText(uniqueOrders.size() + " sales");

            BigDecimal avgOrder = uniqueOrders.isEmpty() ? BigDecimal.ZERO :
                    totalRevenue.divide(BigDecimal.valueOf(uniqueOrders.size()), 2, RoundingMode.HALF_UP);
            lblPeriodAvg.setText("LKR " + currencyFormat.format(avgOrder));

            lblRecordCount.setText(list.size() + " itemized entries (" + uniqueOrders.size() + " sales orders)");

        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Database Error", "Failed to load audit records: " + e.getMessage());
        }
    }

    @FXML
    public void handleFilterData(ActionEvent event) {
        loadItemizedSalesData();
    }

    @FXML
    public void handleViewReport(ActionEvent event) {
        generateJasperReport(false);
    }

    @FXML
    public void handleExportPdf(ActionEvent event) {
        generateJasperReport(true);
    }

    private void generateJasperReport(boolean exportPdf) {
        String reportType = cmbReportType.getValue();
        if (reportType == null) {
            AlertUtil.showWarning("Report Selection", "Please select a report type.");
            return;
        }

        try {
            String reportFile = switch (reportType) {
                case "Itemized Counter Sales & Transaction Audit" -> "/reports/daily_sales_report.jrxml";
                case "Ingredient Inventory & Low Stock Alert" -> "/reports/ingredient_stock_report.jrxml";
                case "Wholesale Shop Deliveries Summary" -> "/reports/delivery_summary_report.jrxml";
                case "Production & Manufacturing Report" -> "/reports/production_summary_report.jrxml";
                default -> "/reports/daily_sales_report.jrxml";
            };

            InputStream reportStream = getClass().getResourceAsStream(reportFile);
            if (reportStream == null) {
                AlertUtil.showError("Template Error", "Report template not found: " + reportFile);
                return;
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("FROM_DATE", Date.valueOf(dtFromDate.getValue() != null ? dtFromDate.getValue() : LocalDate.now().minusDays(30)));
            parameters.put("TO_DATE", Date.valueOf(dtToDate.getValue() != null ? dtToDate.getValue() : LocalDate.now().plusDays(1)));

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport, parameters, DBConnection.getInstance().getConnection()
            );

            if (exportPdf) {
                String userHome = System.getProperty("user.home");
                File desktop = new File(userHome, "Desktop");
                File targetDir = desktop.exists() ? desktop : new File(userHome);
                File pdfFile = new File(targetDir, reportType.replaceAll("[^a-zA-Z0-9]+", "_") + "_" + System.currentTimeMillis() + ".pdf");

                JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFile.getAbsolutePath());
                AlertUtil.showInfo("Report PDF Exported", "Professional report exported successfully to:\n" + pdfFile.getAbsolutePath());

                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                    Desktop.getDesktop().open(pdfFile);
                }
            } else {
                // View report directly
                File tempPdf = File.createTempFile("Vishmi_Report_", ".pdf");
                tempPdf.deleteOnExit();
                JasperExportManager.exportReportToPdfFile(jasperPrint, tempPdf.getAbsolutePath());
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                    Desktop.getDesktop().open(tempPdf);
                } else {
                    JasperViewer.viewReport(jasperPrint, false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showError("Report Error", "Failed to generate report: " + e.getMessage());
        }
    }

    public static class ItemizedSaleRecord {
        private final javafx.beans.property.SimpleStringProperty saleId;
        private final javafx.beans.property.SimpleStringProperty soldAt;
        private final javafx.beans.property.SimpleStringProperty cashierName;
        private final javafx.beans.property.SimpleStringProperty productName;
        private final javafx.beans.property.SimpleStringProperty category;
        private final javafx.beans.property.SimpleStringProperty qty;
        private final javafx.beans.property.SimpleStringProperty unitPrice;
        private final javafx.beans.property.SimpleStringProperty lineTotal;
        private final javafx.beans.property.SimpleStringProperty cashReceived;
        private final javafx.beans.property.SimpleStringProperty change;

        public ItemizedSaleRecord(String saleId, String soldAt, String cashierName, String productName,
                                  String category, String qty, String unitPrice, String lineTotal,
                                  String cashReceived, String change) {
            this.saleId = new javafx.beans.property.SimpleStringProperty(saleId);
            this.soldAt = new javafx.beans.property.SimpleStringProperty(soldAt);
            this.cashierName = new javafx.beans.property.SimpleStringProperty(cashierName);
            this.productName = new javafx.beans.property.SimpleStringProperty(productName);
            this.category = new javafx.beans.property.SimpleStringProperty(category);
            this.qty = new javafx.beans.property.SimpleStringProperty(qty);
            this.unitPrice = new javafx.beans.property.SimpleStringProperty(unitPrice);
            this.lineTotal = new javafx.beans.property.SimpleStringProperty(lineTotal);
            this.cashReceived = new javafx.beans.property.SimpleStringProperty(cashReceived);
            this.change = new javafx.beans.property.SimpleStringProperty(change);
        }

        public javafx.beans.property.SimpleStringProperty saleIdProperty() { return saleId; }
        public javafx.beans.property.SimpleStringProperty soldAtProperty() { return soldAt; }
        public javafx.beans.property.SimpleStringProperty cashierNameProperty() { return cashierName; }
        public javafx.beans.property.SimpleStringProperty productNameProperty() { return productName; }
        public javafx.beans.property.SimpleStringProperty categoryProperty() { return category; }
        public javafx.beans.property.SimpleStringProperty qtyProperty() { return qty; }
        public javafx.beans.property.SimpleStringProperty unitPriceProperty() { return unitPrice; }
        public javafx.beans.property.SimpleStringProperty lineTotalProperty() { return lineTotal; }
        public javafx.beans.property.SimpleStringProperty cashReceivedProperty() { return cashReceived; }
        public javafx.beans.property.SimpleStringProperty changeProperty() { return change; }
    }
}
