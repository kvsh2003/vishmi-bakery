package lk.ijse.vishmibakehouse.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import lk.ijse.vishmibakehouse.bo.BOFactory;
import lk.ijse.vishmibakehouse.bo.BOTypes;
import lk.ijse.vishmibakehouse.bo.custom.DeliveryBO;
import lk.ijse.vishmibakehouse.bo.custom.EmployeeBO;
import lk.ijse.vishmibakehouse.dto.*;
import lk.ijse.vishmibakehouse.util.AlertUtil;
import lk.ijse.vishmibakehouse.util.DialogUtil;
import lk.ijse.vishmibakehouse.util.ValidationUtil;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DeliveriesController {

    @FXML
    private ComboBox<VehicleDTO> cmbVehicle;

    @FXML
    private ComboBox<EmployeeDTO> cmbDriver;

    @FXML
    private ComboBox<String> cmbStatus;

    @FXML
    private TableView<DeliverySummaryDTO> tblDeliveries;

    @FXML
    private TableColumn<DeliverySummaryDTO, Long> colId;
    @FXML
    private TableColumn<DeliverySummaryDTO, String> colShop;
    @FXML
    private TableColumn<DeliverySummaryDTO, String> colVehicle;
    @FXML
    private TableColumn<DeliverySummaryDTO, String> colDriver;
    @FXML
    private TableColumn<DeliverySummaryDTO, Date> colDate;
    @FXML
    private TableColumn<DeliverySummaryDTO, BigDecimal> colPacked;
    @FXML
    private TableColumn<DeliverySummaryDTO, BigDecimal> colDelivered;
    @FXML
    private TableColumn<DeliverySummaryDTO, String> colStatus;

    private final DeliveryBO deliveryBO = BOFactory.getInstance().getBO(BOTypes.DELIVERY);
    private final EmployeeBO employeeBO = BOFactory.getInstance().getBO(BOTypes.EMPLOYEE);

    private DeliverySummaryDTO selectedDelivery;

    @FXML
    public void initialize() {
        cmbStatus.setItems(FXCollections.observableArrayList("PACKED", "DISPATCHED", "DELIVERED", "CANCELLED"));

        colId.setCellValueFactory(new PropertyValueFactory<>("deliveryId"));
        colShop.setCellValueFactory(new PropertyValueFactory<>("shopName"));
        colVehicle.setCellValueFactory(new PropertyValueFactory<>("registrationNo"));
        colDriver.setCellValueFactory(new PropertyValueFactory<>("deliveryStaff"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("deliveryDate"));
        colPacked.setCellValueFactory(new PropertyValueFactory<>("packedQty"));
        colDelivered.setCellValueFactory(new PropertyValueFactory<>("deliveredQty"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadVehiclesAndStaff();
        loadDeliveriesTable();

        tblDeliveries.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedDelivery = newVal;
                cmbStatus.getSelectionModel().select(newVal.getStatus());
            }
        });
    }

    private void loadVehiclesAndStaff() {
        try {
            List<VehicleDTO> vehicles = deliveryBO.getActiveVehicles();
            cmbVehicle.setItems(FXCollections.observableArrayList(vehicles));

            List<EmployeeDTO> employees = employeeBO.getActiveEmployees();
            cmbDriver.setItems(FXCollections.observableArrayList(employees));
            cmbDriver.setConverter(new StringConverter<>() {
                @Override public String toString(EmployeeDTO object) { return object != null ? object.getName() + " (" + object.getJobRole() + ")" : ""; }
                @Override public EmployeeDTO fromString(String string) { return null; }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadDeliveriesTable() {
        try {
            List<DeliverySummaryDTO> list = deliveryBO.getDeliverySummaries();
            tblDeliveries.setItems(FXCollections.observableArrayList(list));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleUpdateStatus(ActionEvent event) {
        if (selectedDelivery == null) {
            AlertUtil.showWarning("Selection Required", "Please select a delivery from the table.");
            return;
        }

        String status = cmbStatus.getValue();
        Long vehicleId = cmbVehicle.getValue() != null ? cmbVehicle.getValue().getVehicleId() : null;
        Long driverId = cmbDriver.getValue() != null ? cmbDriver.getValue().getEmployeeId() : null;

        if (("DISPATCHED".equals(status) || "DELIVERED".equals(status)) && (vehicleId == null || driverId == null)) {
            AlertUtil.showWarning("Vehicle & Driver Required", "A vehicle and delivery staff must be assigned before dispatch or delivery completion.");
            return;
        }

        try {
            if (deliveryBO.dispatchDelivery(selectedDelivery.getDeliveryId(), vehicleId != null ? vehicleId : 1, driverId != null ? driverId : 10)) {
                // Update status in delivery table
                Optional<DeliveryDTO> opt = deliveryBO.findDeliveryById(selectedDelivery.getDeliveryId());
                if (opt.isPresent()) {
                    DeliveryDTO d = opt.get();
                    d.setStatus(status);
                    d.setVehicleId(vehicleId);
                    d.setDeliveryEmployeeId(driverId);
                    // update status
                }
                AlertUtil.showInfo("Updated", "Delivery status updated successfully.");
                loadDeliveriesTable();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Database Error", e.getMessage());
        }
    }

    @FXML
    public void handleMarkDelivered(ActionEvent event) {
        if (selectedDelivery == null) {
            AlertUtil.showWarning("Selection Required", "Please select a delivery to confirm complete.");
            return;
        }

        try {
            Optional<DeliveryDTO> opt = deliveryBO.findDeliveryById(selectedDelivery.getDeliveryId());
            if (opt.isPresent()) {
                DeliveryDTO dto = opt.get();
                List<Long> itemIds = new ArrayList<>();
                List<BigDecimal> deliveredQtys = new ArrayList<>();

                for (DeliveryItemDTO item : dto.getItems()) {
                    itemIds.add(item.getDeliveryItemId());
                    deliveredQtys.add(item.getPackedQty()); // Full delivery
                }

                if (deliveryBO.completeDelivery(selectedDelivery.getDeliveryId(), itemIds, deliveredQtys)) {
                    AlertUtil.showInfo("Delivery Completed", "Delivery #" + selectedDelivery.getDeliveryId() + " marked as DELIVERED.");
                    loadDeliveriesTable();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Database Error", e.getMessage());
        }
    }

    @FXML
    public void handleRefreshDeliveries(ActionEvent event) {
        loadDeliveriesTable();
    }

    @FXML
    public void handleManageShops(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Wholesale Destination Shops");
        dialog.setHeaderText("Add New Wholesale Destination Shop");

        VBox form = new VBox(12);
        form.setPrefWidth(420);
        form.setPadding(new Insets(10, 0, 10, 0));

        VBox boxName = new VBox(4);
        Label lblName = new Label("Shop Name *");
        lblName.getStyleClass().add("field-label");
        TextField txtShopName = new TextField();
        txtShopName.setPromptText("e.g. Lanka Supermart");
        boxName.getChildren().addAll(lblName, txtShopName);

        VBox boxPhone = new VBox(4);
        Label lblPhone = new Label("Contact Phone Number");
        lblPhone.getStyleClass().add("field-label");
        TextField txtShopPhone = new TextField();
        txtShopPhone.setPromptText("e.g. 011-2345678");
        boxPhone.getChildren().addAll(lblPhone, txtShopPhone);

        VBox boxAddress = new VBox(4);
        Label lblAddress = new Label("Full Delivery Address *");
        lblAddress.getStyleClass().add("field-label");
        TextField txtShopAddress = new TextField();
        txtShopAddress.setPromptText("e.g. 142 High Level Road, Nugegoda");
        boxAddress.getChildren().addAll(lblAddress, txtShopAddress);

        form.getChildren().addAll(boxName, boxPhone, boxAddress);

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button btnOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        if (btnOk != null) {
            btnOk.setText("Save Shop");
        }

        DialogUtil.styleAndCenter(dialog);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String name = txtShopName.getText().trim();
            String phone = txtShopPhone.getText().trim();
            String address = txtShopAddress.getText().trim();

            if (name.isEmpty() || address.isEmpty()) {
                AlertUtil.showWarning("Validation Error", "Shop name and address are required.");
                return;
            }

            try {
                if (deliveryBO.saveShop(new ShopDTO(0, name, phone, address, true))) {
                    AlertUtil.showInfo("Success", "Shop '" + name + "' added successfully.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                AlertUtil.showError("Database Error", e.getMessage());
            }
        }
    }

    @FXML
    public void handleManageVehicles(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Delivery Fleet Registration");
        dialog.setHeaderText("Register Delivery Three-Wheeler");

        VBox form = new VBox(12);
        form.setPrefWidth(420);
        form.setPadding(new Insets(10, 0, 10, 0));

        VBox boxReg = new VBox(4);
        Label lblReg = new Label("Registration No *");
        lblReg.getStyleClass().add("field-label");
        TextField txtRegNo = new TextField();
        txtRegNo.setPromptText("e.g. WP BC-4567");
        boxReg.getChildren().addAll(lblReg, txtRegNo);

        VBox boxModel = new VBox(4);
        Label lblModel = new Label("Vehicle Model *");
        lblModel.getStyleClass().add("field-label");
        TextField txtModel = new TextField();
        txtModel.setPromptText("e.g. Bajaj RE 4-Stroke Green");
        boxModel.getChildren().addAll(lblModel, txtModel);

        form.getChildren().addAll(boxReg, boxModel);

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button btnOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        if (btnOk != null) {
            btnOk.setText("Register Vehicle");
        }

        DialogUtil.styleAndCenter(dialog);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String reg = txtRegNo.getText().trim();
            String model = txtModel.getText().trim();

            if (reg.isEmpty() || model.isEmpty()) {
                AlertUtil.showWarning("Validation Error", "Registration number and model are required.");
                return;
            }

            try {
                if (deliveryBO.saveVehicle(new VehicleDTO(0, reg, model, true))) {
                    AlertUtil.showInfo("Success", "Vehicle '" + reg + "' registered successfully.");
                    loadVehiclesAndStaff();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                AlertUtil.showError("Database Error", e.getMessage());
            }
        }
    }
}
