package lk.ijse.vishmibakehouse.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import lk.ijse.vishmibakehouse.bo.BOFactory;
import lk.ijse.vishmibakehouse.bo.BOTypes;
import lk.ijse.vishmibakehouse.bo.custom.ProductBO;
import lk.ijse.vishmibakehouse.bo.custom.SpecialOrderBO;
import lk.ijse.vishmibakehouse.dto.*;
import lk.ijse.vishmibakehouse.util.AlertUtil;
import lk.ijse.vishmibakehouse.util.DialogUtil;
import lk.ijse.vishmibakehouse.util.SecurityContext;
import lk.ijse.vishmibakehouse.util.ValidationUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpecialOrdersController {

    @FXML
    private ComboBox<CustomerDTO> cmbCustomer;

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
    private ComboBox<ProductDTO> cmbProductItem;

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
    private final ProductBO productBO = BOFactory.getInstance().getBO(BOTypes.PRODUCT);

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

        colOrderStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if ("CONFIRMED".equalsIgnoreCase(status) || "COMPLETED".equalsIgnoreCase(status)) {
                        setStyle("-fx-text-fill: #059669; -fx-font-weight: bold;");
                    } else if ("PENDING".equalsIgnoreCase(status) || "IN_PROGRESS".equalsIgnoreCase(status)) {
                        setStyle("-fx-text-fill: #D97706; -fx-font-weight: bold;");
                    } else if ("READY".equalsIgnoreCase(status)) {
                        setStyle("-fx-text-fill: #2563EB; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold;");
                    }
                }
            }
        });

        txtDeposit.textProperty().addListener((obs, oldVal, newVal) -> recalculateTotals());

        loadCustomers();
        loadCatalogProducts();
        loadOrdersTable();

        // Customer selection listener
        cmbCustomer.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.getCustomerId() > 0) {
                txtCustomerName.setText(newVal.getName());
                txtCustomerPhone.setText(newVal.getPhone() != null ? newVal.getPhone() : "");
                txtCustomerAddress.setText(newVal.getAddress() != null ? newVal.getAddress() : "");
            }
        });

        // Catalog product item selection listener
        cmbProductItem.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (newVal.getProductId() > 0) {
                    txtItemDesc.setText(newVal.getName());
                    txtItemPrice.setText(newVal.getSellingPrice() != null ? newVal.getSellingPrice().toString() : "0.00");
                    if (txtItemQty.getText() == null || txtItemQty.getText().trim().isEmpty()) {
                        txtItemQty.setText("1");
                    }
                } else {
                    txtItemDesc.clear();
                    txtItemPrice.clear();
                    txtItemQty.setText("1");
                }
            }
        });

        // Phone number auto-search for returning customers
        txtCustomerPhone.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.trim().length() >= 10 && (txtCustomerName.getText() == null || txtCustomerName.getText().trim().isEmpty())) {
                try {
                    Optional<CustomerDTO> found = specialOrderBO.findCustomerByPhone(newVal.trim());
                    if (found.isPresent()) {
                        CustomerDTO c = found.get();
                        txtCustomerName.setText(c.getName());
                        txtCustomerAddress.setText(c.getAddress() != null ? c.getAddress() : "");
                        for (CustomerDTO item : cmbCustomer.getItems()) {
                            if (item.getCustomerId() == c.getCustomerId()) {
                                cmbCustomer.getSelectionModel().select(item);
                                break;
                            }
                        }
                    }
                } catch (SQLException ignored) {
                }
            }
        });

        cmbStatusFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            filterOrders(newVal);
        });
    }

    private void loadCustomers() {
        try {
            List<CustomerDTO> list = specialOrderBO.getAllCustomers();
            List<CustomerDTO> displayList = new ArrayList<>();
            displayList.add(new CustomerDTO(0, "-- Select Existing Customer --", "", ""));
            displayList.addAll(list);

            cmbCustomer.setItems(FXCollections.observableArrayList(displayList));
            cmbCustomer.setConverter(new StringConverter<>() {
                @Override
                public String toString(CustomerDTO c) {
                    if (c == null) return "";
                    if (c.getCustomerId() <= 0) return c.getName();
                    return c.getName() + " (" + (c.getPhone() != null ? c.getPhone() : "No Phone") + ")";
                }

                @Override
                public CustomerDTO fromString(String string) {
                    return null;
                }
            });
            cmbCustomer.getSelectionModel().selectFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadCatalogProducts() {
        try {
            List<ProductDTO> products = productBO.getActiveProducts();
            List<ProductDTO> displayList = new ArrayList<>();
            displayList.add(new ProductDTO(0, "-- Custom Cake / Custom Design --", "", "PIECE", BigDecimal.ZERO, true));
            displayList.addAll(products);

            cmbProductItem.setItems(FXCollections.observableArrayList(displayList));
            cmbProductItem.setConverter(new StringConverter<>() {
                @Override
                public String toString(ProductDTO p) {
                    if (p == null) return "";
                    if (p.getProductId() <= 0) return p.getName();
                    return p.getName() + " (LKR " + currencyFormat.format(p.getSellingPrice()) + ")";
                }

                @Override
                public ProductDTO fromString(String string) {
                    return null;
                }
            });
            cmbProductItem.getSelectionModel().selectFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
    public void handleNewCustomer(ActionEvent event) {
        cmbCustomer.getSelectionModel().selectFirst();
        txtCustomerName.clear();
        txtCustomerPhone.clear();
        txtCustomerAddress.clear();
        txtCustomerName.requestFocus();
    }

    @FXML
    public void handleSaveCustomer(ActionEvent event) {
        String name = txtCustomerName.getText() != null ? txtCustomerName.getText().trim() : "";
        String phone = txtCustomerPhone.getText() != null ? txtCustomerPhone.getText().trim() : "";
        String address = txtCustomerAddress.getText() != null ? txtCustomerAddress.getText().trim() : "";

        if (name.isEmpty() || phone.isEmpty()) {
            AlertUtil.showWarning("Customer Details Required", "Customer Name and Contact Phone are required to register.");
            return;
        }

        try {
            Optional<CustomerDTO> existing = specialOrderBO.findCustomerByPhone(phone);
            if (existing.isPresent()) {
                CustomerDTO cust = existing.get();
                cust.setName(name);
                cust.setAddress(address);
                specialOrderBO.updateCustomer(cust);
                AlertUtil.showInfo("Customer Updated", "Customer '" + name + "' details updated in DB.");
            } else {
                CustomerDTO newCust = new CustomerDTO(0, name, phone, address);
                specialOrderBO.saveCustomer(newCust);
                AlertUtil.showInfo("Customer Registered", "Customer '" + name + "' successfully saved to DB.");
            }

            loadCustomers();
            // Select the saved customer in the dropdown
            for (CustomerDTO c : cmbCustomer.getItems()) {
                if (phone.equals(c.getPhone())) {
                    cmbCustomer.getSelectionModel().select(c);
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Database Error", "Failed to save customer: " + e.getMessage());
        }
    }

    @FXML
    public void handleAddItem(ActionEvent event) {
        String desc = txtItemDesc.getText() != null ? txtItemDesc.getText().trim() : "";
        if (desc.isEmpty()) {
            AlertUtil.showWarning("Description Required", "Please enter cake item description or select an item from DB.");
            return;
        }
        if (!ValidationUtil.isPositiveDecimal(txtItemQty.getText())) {
            AlertUtil.showWarning("Quantity Required", "Please enter a valid positive quantity.");
            return;
        }
        if (!ValidationUtil.isPositiveDecimal(txtItemPrice.getText())) {
            AlertUtil.showWarning("Price Required", "Please enter a valid positive unit price.");
            return;
        }

        BigDecimal qty = new BigDecimal(txtItemQty.getText().trim());
        BigDecimal price = new BigDecimal(txtItemPrice.getText().trim());
        BigDecimal total = qty.multiply(price);

        Long prodId = null;
        String prodName = "";
        ProductDTO selectedProduct = cmbProductItem.getValue();
        if (selectedProduct != null && selectedProduct.getProductId() > 0) {
            prodId = selectedProduct.getProductId();
            prodName = selectedProduct.getName();
        }

        orderItems.add(new SpecialOrderItemDTO(0, 0, prodId, prodName, desc, qty, price, total));
        recalculateTotals();

        // Reset item fields
        cmbProductItem.getSelectionModel().selectFirst();
        txtItemDesc.clear();
        txtItemQty.setText("1");
        txtItemPrice.clear();
    }

    @FXML
    public void handleRemoveItem(ActionEvent event) {
        SpecialOrderItemDTO selected = tblOrderItems.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("Selection Required", "Please select an item from the list to remove.");
            return;
        }
        orderItems.remove(selected);
        recalculateTotals();
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
            AlertUtil.showWarning("Date Required", "Please select cake required delivery date.");
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
            // Find or create/update customer in DB
            long customerId = 1;
            Optional<CustomerDTO> optCust = specialOrderBO.findCustomerByPhone(custPhone);
            if (optCust.isPresent()) {
                customerId = optCust.get().getCustomerId();
                CustomerDTO existingCust = optCust.get();
                if (!custName.equals(existingCust.getName()) || !custAddr.equals(existingCust.getAddress())) {
                    existingCust.setName(custName);
                    existingCust.setAddress(custAddr);
                    specialOrderBO.updateCustomer(existingCust);
                }
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
                orderDTO.getItems().add(new SpecialOrderItemDTO(0, 0, item.getProductId(), item.getProductName(), item.getDescription(), item.getQuantity(), item.getUnitPrice(), item.getLineTotal()));
            }

            if (specialOrderBO.placeSpecialOrder(orderDTO)) {
                AlertUtil.showInfo("Order Placed", "Special Order #" + orderDTO.getSpecialOrderId() + " registered successfully.");
                clearOrderForm();
                loadCustomers();
                loadOrdersTable();
            } else {
                AlertUtil.showError("Order Failed", "Could not complete registration of special order.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Database Error", e.getMessage());
        }
    }

    private void clearOrderForm() {
        cmbCustomer.getSelectionModel().selectFirst();
        txtCustomerName.clear();
        txtCustomerPhone.clear();
        txtCustomerAddress.clear();
        cmbOrderType.getSelectionModel().selectFirst();
        dtRequiredDate.setValue(LocalDate.now().plusDays(3));
        txtDeposit.clear();
        txtInstructions.clear();
        cmbProductItem.getSelectionModel().selectFirst();
        txtItemDesc.clear();
        txtItemQty.setText("1");
        txtItemPrice.clear();
        orderItems.clear();
        recalculateTotals();
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

        DialogUtil.styleAndCenter(dialog);

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
