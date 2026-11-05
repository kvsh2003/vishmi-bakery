package lk.ijse.vishmibakehouse.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.vishmibakehouse.bo.BOFactory;
import lk.ijse.vishmibakehouse.bo.BOTypes;
import lk.ijse.vishmibakehouse.bo.custom.SpecialOrderBO;
import lk.ijse.vishmibakehouse.dto.CustomerDTO;
import lk.ijse.vishmibakehouse.dto.SpecialOrderDTO;
import lk.ijse.vishmibakehouse.dto.SpecialOrderItemDTO;
import lk.ijse.vishmibakehouse.util.AlertUtil;
import lk.ijse.vishmibakehouse.util.SecurityContext;
import lk.ijse.vishmibakehouse.util.ValidationUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class SpecialOrdersController {

    @FXML
    private TextField txtCustomerName;

    @FXML
    private TextField txtCustomerPhone;

    @FXML
    private TextField txtCustomerAddress;

    @FXML
    private ComboBox<String> cmbOrderType;

    @FXML
    private DatePicker dtRequiredDate;

    @FXML
    private TextField txtDeposit;

    @FXML
    private TextField txtInstructions;

    @FXML
    private TextField txtItemDesc;

    @FXML
    private TextField txtItemQty;

    @FXML
    private TextField txtItemPrice;

    @FXML
    private TableView<SpecialOrderItemDTO> tblOrderItems;
    @FXML
    private TableColumn<SpecialOrderItemDTO, String> colItemDesc;
    @FXML
    private TableColumn<SpecialOrderItemDTO, BigDecimal> colItemQty;
    @FXML
    private TableColumn<SpecialOrderItemDTO, BigDecimal> colItemPrice;

    @FXML
    private Label lblOrderTotal;

    @FXML
    private Label lblOrderBalance;

    @FXML
    private ComboBox<String> cmbStatusFilter;

    @FXML
    private TableView<SpecialOrderDTO> tblSpecialOrders;
    @FXML
    private TableColumn<SpecialOrderDTO, Long> colOrderId;
    @FXML
    private TableColumn<SpecialOrderDTO, String> colOrderCustomer;
    @FXML
    private TableColumn<SpecialOrderDTO, String> colOrderType;
    @FXML
    private TableColumn<SpecialOrderDTO, Timestamp> colOrderDate;
    @FXML
    private TableColumn<SpecialOrderDTO, BigDecimal> colOrderTotal;
    @FXML
    private TableColumn<SpecialOrderDTO, BigDecimal> colOrderDeposit;
    @FXML
    private TableColumn<SpecialOrderDTO, BigDecimal> colOrderBalance;
    @FXML
    private TableColumn<SpecialOrderDTO, String> colOrderStatus;

    private final SpecialOrderBO specialOrderBO = BOFactory.getInstance().getBO(BOTypes.SPECIAL_ORDER);
    private final ObservableList<SpecialOrderItemDTO> orderItems = FXCollections.observableArrayList();
    private final DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");
    private BigDecimal currentTotal = BigDecimal.ZERO;

    @FXML
    public void initialize() {
        cmbOrderType.setItems(FXCollections.observableArrayList("WEDDING_CAKE", "SPECIAL"));
        cmbOrderType.getSelectionModel().selectFirst();

        dtRequiredDate.setValue(LocalDate.now().plusDays(3));

        cmbStatusFilter.setItems(FXCollections.observableArrayList("ALL", "PENDING", "CONFIRMED", "IN_PROGRESS", "READY", "COMPLETED", "CANCELLED"));
        cmbStatusFilter.getSelectionModel().selectFirst();

        colItemDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colItemQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colItemPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        tblOrderItems.setItems(orderItems);

        colOrderId.setCellValueFactory(new PropertyValueFactory<>("specialOrderId"));
        colOrderCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colOrderType.setCellValueFactory(new PropertyValueFactory<>("orderType"));
        colOrderDate.setCellValueFactory(new PropertyValueFactory<>("requiredAt"));
        colOrderTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colOrderDeposit.setCellValueFactory(new PropertyValueFactory<>("depositAmount"));
        colOrderBalance.setCellValueFactory(new PropertyValueFactory<>("balanceAmount"));
        colOrderStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        txtDeposit.textProperty().addListener((obs, oldVal, newVal) -> recalculateTotals());

        loadOrdersTable();

        cmbStatusFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            filterOrders(newVal);
        });
    }

    private void loadOrdersTable() {
        try {
            List<SpecialOrderDTO> list = specialOrderBO.getAllOrders();
            tblSpecialOrders.setItems(FXCollections.observableArrayList(list));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void filterOrders(String status) {
        try {
            List<SpecialOrderDTO> list = (status == null || "ALL".equals(status))
                    ? specialOrderBO.getAllOrders()
                    : specialOrderBO.getOrdersByStatus(status);
            tblSpecialOrders.setItems(FXCollections.observableArrayList(list));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleAddItem(ActionEvent event) {
        String desc = txtItemDesc.getText() != null ? txtItemDesc.getText().trim() : "";
        if (desc.isEmpty()) {
            AlertUtil.showWarning("Description Required", "Please enter cake item description.");
            return;
        }
        if (!ValidationUtil.isPositiveDecimal(txtItemQty.getText())) {
            AlertUtil.showWarning("Quantity Required", "Please enter positive quantity.");
            return;
        }
        if (!ValidationUtil.isPositiveDecimal(txtItemPrice.getText())) {
            AlertUtil.showWarning("Price Required", "Please enter positive unit price.");
            return;
        }

        BigDecimal qty = new BigDecimal(txtItemQty.getText().trim());
        BigDecimal price = new BigDecimal(txtItemPrice.getText().trim());
        BigDecimal total = qty.multiply(price);

        orderItems.add(new SpecialOrderItemDTO(0, 0, null, "", desc, qty, price, total));
        recalculateTotals();

        txtItemDesc.clear();
        txtItemQty.clear();
        txtItemPrice.clear();
    }

    private void recalculateTotals() {
        currentTotal = BigDecimal.ZERO;
        for (SpecialOrderItemDTO item : orderItems) {
            currentTotal = currentTotal.add(item.getLineTotal());
        }
        lblOrderTotal.setText("LKR " + currencyFormat.format(currentTotal));

        BigDecimal deposit = BigDecimal.ZERO;
        if (ValidationUtil.isNonNegativeDecimal(txtDeposit.getText())) {
            deposit = new BigDecimal(txtDeposit.getText().trim());
        }
        BigDecimal balance = currentTotal.subtract(deposit);
        lblOrderBalance.setText("LKR " + currencyFormat.format(balance));
    }

    @FXML
    public void handlePlaceOrder(ActionEvent event) {
        String custName = txtCustomerName.getText() != null ? txtCustomerName.getText().trim() : "";
        String custPhone = txtCustomerPhone.getText() != null ? txtCustomerPhone.getText().trim() : "";
        String custAddr = txtCustomerAddress.getText() != null ? txtCustomerAddress.getText().trim() : "";

        if (custName.isEmpty() || custPhone.isEmpty()) {
            AlertUtil.showWarning("Customer Required", "Customer Name and Contact Phone are required.");
            return;
        }
        if (dtRequiredDate.getValue() == null) {
            AlertUtil.showWarning("Date Required", "Please select cake required date.");
            return;
        }
        if (orderItems.isEmpty()) {
            AlertUtil.showWarning("Items Required", "Please add at least one item specification to this special order.");
            return;
        }

        BigDecimal deposit = BigDecimal.ZERO;
        if (ValidationUtil.isNonNegativeDecimal(txtDeposit.getText())) {
            deposit = new BigDecimal(txtDeposit.getText().trim());
        }

        try {
            // Find or create customer
            long customerId = 1;
            Optional<CustomerDTO> optCust = specialOrderBO.findCustomerByPhone(custPhone);
            if (optCust.isPresent()) {
                customerId = optCust.get().getCustomerId();
            } else {
                CustomerDTO newCust = new CustomerDTO(0, custName, custPhone, custAddr);
                specialOrderBO.saveCustomer(newCust);
                Optional<CustomerDTO> savedCust = specialOrderBO.findCustomerByPhone(custPhone);
                if (savedCust.isPresent()) {
                    customerId = savedCust.get().getCustomerId();
                }
            }

            long empId = (SecurityContext.getCurrentUser() != null) ? SecurityContext.getCurrentUser().getEmployeeId() : 1;

            SpecialOrderDTO orderDTO = new SpecialOrderDTO(
                    0,
                    customerId,
                    custName,
                    custPhone,
                    empId,
                    "",
                    cmbOrderType.getValue(),
                    Timestamp.valueOf(dtRequiredDate.getValue().atStartOfDay()),
                    "CONFIRMED",
                    txtInstructions.getText() != null ? txtInstructions.getText().trim() : "",
                    deposit,
                    currentTotal,
                    currentTotal.subtract(deposit)
            );

            for (SpecialOrderItemDTO item : orderItems) {
                orderDTO.getItems().add(new SpecialOrderItemDTO(0, 0, null, "", item.getDescription(), item.getQuantity(), item.getUnitPrice(), item.getLineTotal()));
            }

            if (specialOrderBO.placeSpecialOrder(orderDTO)) {
                AlertUtil.showInfo("Order Placed", "Special Order #" + orderDTO.getSpecialOrderId() + " registered successfully.");
                orderItems.clear();
                recalculateTotals();
                txtCustomerName.clear();
                txtCustomerPhone.clear();
                txtCustomerAddress.clear();
                txtInstructions.clear();
                txtDeposit.clear();
                loadOrdersTable();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Database Error", e.getMessage());
        }
    }

    @FXML
    public void handleUpdateStatus(ActionEvent event) {
        SpecialOrderDTO selected = tblSpecialOrders.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("Selection Required", "Please select an order from the table.");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(selected.getStatus(), "PENDING", "CONFIRMED", "IN_PROGRESS", "READY", "COMPLETED", "CANCELLED");
        dialog.setTitle("Update Order Status");
        dialog.setHeaderText("Change status for Special Order #" + selected.getSpecialOrderId());
        dialog.setContentText("Select new status:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(status -> {
            try {
                if (specialOrderBO.updateOrderStatus(selected.getSpecialOrderId(), status)) {
                    AlertUtil.showInfo("Status Updated", "Order #" + selected.getSpecialOrderId() + " status changed to " + status);
                    loadOrdersTable();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                AlertUtil.showError("Database Error", e.getMessage());
            }
        });
    }
}
