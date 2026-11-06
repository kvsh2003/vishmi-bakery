package lk.ijse.vishmibakehouse.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.vishmibakehouse.bo.BOFactory;
import lk.ijse.vishmibakehouse.bo.BOTypes;
import lk.ijse.vishmibakehouse.bo.custom.DashboardBO;
import lk.ijse.vishmibakehouse.dto.DashboardSummaryDTO;
import lk.ijse.vishmibakehouse.dto.DeliverySummaryDTO;
import lk.ijse.vishmibakehouse.dto.IngredientStockDTO;
import lk.ijse.vishmibakehouse.dto.ProductAvailabilityDTO;
import lk.ijse.vishmibakehouse.dto.ProductionBatchDTO;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardController {

    @FXML
    private Label lblTodayDate;

    @FXML
    private Label lblMetricSales;

    @FXML
    private Label lblMetricProduction;

    @FXML
    private Label lblMetricDeliveries;

    @FXML
    private Label lblMetricStockAlerts;

    @FXML
    private Label lblMetricSpecialOrders;

    // Table 1: Today's Production Batches
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

    // Table 2: Critical Stock Reorder Alerts
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

    // Table 3: Finished Goods Counter Stock
    @FXML
    private TableView<ProductAvailabilityDTO> tblProductStock;
    @FXML
    private TableColumn<ProductAvailabilityDTO, String> colProductStockName;
    @FXML
    private TableColumn<ProductAvailabilityDTO, String> colProductStockUnit;
    @FXML
    private TableColumn<ProductAvailabilityDTO, BigDecimal> colProductStockProduced;
    @FXML
    private TableColumn<ProductAvailabilityDTO, BigDecimal> colProductStockSold;
    @FXML
    private TableColumn<ProductAvailabilityDTO, String> colProductStockAvailable;

    // Table 4: Active Shop Deliveries
    @FXML
    private TableView<DeliverySummaryDTO> tblDeliveries;
    @FXML
    private TableColumn<DeliverySummaryDTO, Long> colDeliveryId;
    @FXML
    private TableColumn<DeliverySummaryDTO, String> colDeliveryShop;
    @FXML
    private TableColumn<DeliverySummaryDTO, String> colDeliveryVehicle;
    @FXML
    private TableColumn<DeliverySummaryDTO, String> colDeliveryStaff;
    @FXML
    private TableColumn<DeliverySummaryDTO, String> colDeliveryStatus;

    private final DashboardBO dashboardBO = BOFactory.getInstance().getBO(BOTypes.DASHBOARD);
    private final DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy");

    @FXML
    public void initialize() {
        if (lblTodayDate != null) {
            lblTodayDate.setText(LocalDate.now().format(dateFormatter));
        }

        setupTableColumns();
        loadDashboardData();
    }

    private void setupTableColumns() {
        // Table 1: Production
        colProdBatchId.setCellValueFactory(new PropertyValueFactory<>("productionBatchId"));
        colProdProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colProdPlanned.setCellValueFactory(new PropertyValueFactory<>("plannedQty"));
        colProdActual.setCellValueFactory(new PropertyValueFactory<>("actualQty"));
        colProdStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colProdStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if ("COMPLETED".equalsIgnoreCase(status)) {
                        setStyle("-fx-text-fill: #059669; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #D97706; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Table 2: Low Stock
        colStockIngredient.setCellValueFactory(new PropertyValueFactory<>("name"));
        colStockUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        colStockAvailable.setCellValueFactory(new PropertyValueFactory<>("availableQty"));
        colStockReorder.setCellValueFactory(new PropertyValueFactory<>("reorderLevel"));
        colStockAvailable.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal qty, boolean empty) {
                super.updateItem(qty, empty);
                if (empty || qty == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(qty.toString());
                    setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold;");
                }
            }
        });

        // Table 3: Product Counter Stock
        colProductStockName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colProductStockUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        colProductStockProduced.setCellValueFactory(new PropertyValueFactory<>("producedQty"));
        colProductStockSold.setCellValueFactory(new PropertyValueFactory<>("outletSoldQty"));
        colProductStockAvailable.setCellValueFactory(cellData -> {
            BigDecimal avail = cellData.getValue().getAvailableQty();
            if (avail == null || avail.compareTo(BigDecimal.ZERO) <= 0) {
                return new SimpleStringProperty("0 (Out of Stock)");
            }
            return new SimpleStringProperty(avail.toString());
        });
        colProductStockAvailable.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(val);
                    if (val.contains("Out of Stock")) {
                        setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #15803D; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Table 4: Deliveries
        colDeliveryId.setCellValueFactory(new PropertyValueFactory<>("deliveryId"));
        colDeliveryShop.setCellValueFactory(new PropertyValueFactory<>("shopName"));
        colDeliveryVehicle.setCellValueFactory(new PropertyValueFactory<>("registrationNo"));
        colDeliveryStaff.setCellValueFactory(new PropertyValueFactory<>("deliveryStaff"));
        colDeliveryStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colDeliveryStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if ("DELIVERED".equalsIgnoreCase(status)) {
                        setStyle("-fx-text-fill: #059669; -fx-font-weight: bold;");
                    } else if ("DISPATCHED".equalsIgnoreCase(status)) {
                        setStyle("-fx-text-fill: #2563EB; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #D97706; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void loadDashboardData() {
        try {
            DashboardSummaryDTO summary = dashboardBO.getDashboardSummary();
            lblMetricSales.setText("LKR " + currencyFormat.format(summary.getTodaySales()));
            lblMetricProduction.setText(summary.getUnitsProducedToday() + " units");
            lblMetricDeliveries.setText(summary.getPendingDeliveries() + " orders");
            lblMetricStockAlerts.setText(summary.getLowStockIngredients() + " items");
            if (lblMetricSpecialOrders != null) {
                lblMetricSpecialOrders.setText(summary.getSpecialOrders() + " orders");
            }

            // Table 1: Production
            List<ProductionBatchDTO> batches = dashboardBO.getTodayProduction();
            tblTodayProduction.setItems(FXCollections.observableArrayList(batches));

            // Table 2: Low Stock
            List<IngredientStockDTO> lowStock = dashboardBO.getLowStockAlerts();
            tblLowStock.setItems(FXCollections.observableArrayList(lowStock));

            // Table 3: Product Counter Stock
            List<ProductAvailabilityDTO> products = dashboardBO.getProductAvailability();
            tblProductStock.setItems(FXCollections.observableArrayList(products));

            // Table 4: Deliveries
            List<DeliverySummaryDTO> deliveries = dashboardBO.getRecentDeliveries();
            tblDeliveries.setItems(FXCollections.observableArrayList(deliveries));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleRefresh() {
        loadDashboardData();
    }

    // ==========================================
    // Quick Navigation Action Handlers
    // ==========================================
    @FXML
    public void handleNavPOS() {
        if (MainLayoutController.getInstance() != null) {
            MainLayoutController.getInstance().handleNavOutletSales(null);
        }
    }

    @FXML
    public void handleNavProduction() {
        if (MainLayoutController.getInstance() != null) {
            MainLayoutController.getInstance().handleNavProduction(null);
        }
    }

    @FXML
    public void handleNavPacking() {
        if (MainLayoutController.getInstance() != null) {
            MainLayoutController.getInstance().handleNavPacking(null);
        }
    }

    @FXML
    public void handleNavDeliveries() {
        if (MainLayoutController.getInstance() != null) {
            MainLayoutController.getInstance().handleNavDeliveries(null);
        }
    }

    @FXML
    public void handleNavSpecialOrders() {
        if (MainLayoutController.getInstance() != null) {
            MainLayoutController.getInstance().handleNavSpecialOrders(null);
        }
    }

    @FXML
    public void handleNavPurchases() {
        if (MainLayoutController.getInstance() != null) {
            MainLayoutController.getInstance().handleNavPurchases(null);
        }
    }

    @FXML
    public void handleNavIngredients() {
        if (MainLayoutController.getInstance() != null) {
            MainLayoutController.getInstance().handleNavIngredients(null);
        }
    }

    @FXML
    public void handleNavReports() {
        if (MainLayoutController.getInstance() != null) {
            MainLayoutController.getInstance().handleNavReports(null);
        }
    }
}
