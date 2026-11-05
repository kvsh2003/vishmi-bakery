package lk.ijse.vishmibakehouse.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.vishmibakehouse.bo.BOFactory;
import lk.ijse.vishmibakehouse.bo.BOTypes;
import lk.ijse.vishmibakehouse.bo.custom.DashboardBO;
import lk.ijse.vishmibakehouse.dto.DashboardSummaryDTO;
import lk.ijse.vishmibakehouse.dto.IngredientStockDTO;
import lk.ijse.vishmibakehouse.dto.ProductionBatchDTO;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;

public class DashboardController {

    @FXML
    private Label lblMetricSales;

    @FXML
    private Label lblMetricProduction;

    @FXML
    private Label lblMetricDeliveries;

    @FXML
    private Label lblMetricStockAlerts;

    @FXML
    private TableView<ProductionBatchDTO> tblTodayProduction;
    @FXML
    private TableColumn<ProductionBatchDTO, Long> colProdBatchId;
    @FXML
    private TableColumn<ProductionBatchDTO, String> colProdProduct;
    @FXML
    private TableColumn<ProductionBatchDTO, BigDecimal> colProdPlanned;
    @FXML
    private TableColumn<ProductionBatchDTO, BigDecimal> colProdActual;
    @FXML
    private TableColumn<ProductionBatchDTO, String> colProdStatus;

    @FXML
    private TableView<IngredientStockDTO> tblLowStock;
    @FXML
    private TableColumn<IngredientStockDTO, String> colStockIngredient;
    @FXML
    private TableColumn<IngredientStockDTO, String> colStockUnit;
    @FXML
    private TableColumn<IngredientStockDTO, BigDecimal> colStockAvailable;
    @FXML
    private TableColumn<IngredientStockDTO, BigDecimal> colStockReorder;

    private final DashboardBO dashboardBO = BOFactory.getInstance().getBO(BOTypes.DASHBOARD);
    private final DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");

    @FXML
    public void initialize() {
        colProdBatchId.setCellValueFactory(new PropertyValueFactory<>("productionBatchId"));
        colProdProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colProdPlanned.setCellValueFactory(new PropertyValueFactory<>("plannedQty"));
        colProdActual.setCellValueFactory(new PropertyValueFactory<>("actualQty"));
        colProdStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colStockIngredient.setCellValueFactory(new PropertyValueFactory<>("name"));
        colStockUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        colStockAvailable.setCellValueFactory(new PropertyValueFactory<>("availableQty"));
        colStockReorder.setCellValueFactory(new PropertyValueFactory<>("reorderLevel"));

        loadDashboardData();
    }

    private void loadDashboardData() {
        try {
            DashboardSummaryDTO summary = dashboardBO.getDashboardSummary();
            lblMetricSales.setText("LKR " + currencyFormat.format(summary.getTodaySales()));
            lblMetricProduction.setText(summary.getUnitsProducedToday() + " units");
            lblMetricDeliveries.setText(summary.getPendingDeliveries() + " orders");
            lblMetricStockAlerts.setText(summary.getLowStockIngredients() + " items");

            List<ProductionBatchDTO> batches = dashboardBO.getTodayProduction();
            tblTodayProduction.setItems(FXCollections.observableArrayList(batches));

            List<IngredientStockDTO> lowStock = dashboardBO.getLowStockAlerts();
            tblLowStock.setItems(FXCollections.observableArrayList(lowStock));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleRefresh(ActionEvent event) {
        loadDashboardData();
    }
}
