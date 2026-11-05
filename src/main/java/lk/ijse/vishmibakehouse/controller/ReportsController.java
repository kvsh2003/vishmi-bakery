package lk.ijse.vishmibakehouse.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.vishmibakehouse.dao.SQLUtil;
import lk.ijse.vishmibakehouse.db.DBConnection;
import lk.ijse.vishmibakehouse.util.AlertUtil;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportsController {

    @FXML
    private ComboBox<String> cmbReportType;

    @FXML
    private DatePicker dtFromDate;

    @FXML
    private DatePicker dtToDate;

    @FXML
    private TableView<DailySaleRecord> tblDailySales;

    @FXML
    private TableColumn<DailySaleRecord, Date> colSaleDate;
    @FXML
    private TableColumn<DailySaleRecord, Integer> colTransactions;
    @FXML
    private TableColumn<DailySaleRecord, BigDecimal> colTotalRevenue;

    @FXML
    public void initialize() {
        cmbReportType.setItems(FXCollections.observableArrayList(
                "Daily Sales Summary Report",
                "Ingredient Inventory & Low Stock Alert",
                "Wholesale Shop Deliveries Summary",
                "Production & Leftover Reuse Report"
        ));
        cmbReportType.getSelectionModel().selectFirst();

        dtFromDate.setValue(LocalDate.now().minusDays(7));
        dtToDate.setValue(LocalDate.now());

        colSaleDate.setCellValueFactory(new PropertyValueFactory<>("saleDate"));
        colTransactions.setCellValueFactory(new PropertyValueFactory<>("transactions"));
        colTotalRevenue.setCellValueFactory(new PropertyValueFactory<>("totalRevenue"));

        loadDailySalesPreview();
    }

    private void loadDailySalesPreview() {
        try {
            ResultSet rs = SQLUtil.executeQuery("SELECT * FROM v_daily_sales ORDER BY sale_date DESC");
            List<DailySaleRecord> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new DailySaleRecord(
                        rs.getDate("sale_date"),
                        rs.getInt("transaction_count"),
                        rs.getBigDecimal("total_sales")
                ));
            }
            tblDailySales.setItems(FXCollections.observableArrayList(list));
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                case "Daily Sales Summary Report" -> "/reports/daily_sales_report.jrxml";
                case "Ingredient Inventory & Low Stock Alert" -> "/reports/ingredient_stock_report.jrxml";
                case "Wholesale Shop Deliveries Summary" -> "/reports/delivery_summary_report.jrxml";
                case "Production & Leftover Reuse Report" -> "/reports/production_summary_report.jrxml";
                default -> "/reports/daily_sales_report.jrxml";
            };

            InputStream reportStream = getClass().getResourceAsStream(reportFile);
            if (reportStream == null) {
                AlertUtil.showError("Template Error", "Report template not found: " + reportFile);
                return;
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("FROM_DATE", Date.valueOf(dtFromDate.getValue()));
            parameters.put("TO_DATE", Date.valueOf(dtToDate.getValue()));

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport, parameters, DBConnection.getInstance().getConnection()
            );

            if (exportPdf) {
                String outPath = System.getProperty("user.home") + "/Desktop/" + reportType.replaceAll("\\s+", "_") + ".pdf";
                JasperExportManager.exportReportToPdfFile(jasperPrint, outPath);
                AlertUtil.showInfo("Report Exported", "Report PDF successfully saved to:\n" + outPath);
            } else {
                JasperViewer.viewReport(jasperPrint, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showError("Report Error", "Failed to generate report: " + e.getMessage());
        }
    }

    public static class DailySaleRecord {
        private Date saleDate;
        private int transactions;
        private BigDecimal totalRevenue;

        public DailySaleRecord(Date saleDate, int transactions, BigDecimal totalRevenue) {
            this.saleDate = saleDate;
            this.transactions = transactions;
            this.totalRevenue = totalRevenue;
        }

        public Date getSaleDate() { return saleDate; }
        public int getTransactions() { return transactions; }
        public BigDecimal getTotalRevenue() { return totalRevenue; }
    }
}
