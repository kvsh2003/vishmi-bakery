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
import lk.ijse.vishmibakehouse.bo.custom.EmployeeBO;
import lk.ijse.vishmibakehouse.bo.custom.ProductBO;
import lk.ijse.vishmibakehouse.bo.custom.SaleBO;
import lk.ijse.vishmibakehouse.dto.EmployeeDTO;
import lk.ijse.vishmibakehouse.dto.ProductAvailabilityDTO;
import lk.ijse.vishmibakehouse.dto.ProductDTO;
import lk.ijse.vishmibakehouse.dto.SaleDTO;
import lk.ijse.vishmibakehouse.dto.SaleItemDTO;
import lk.ijse.vishmibakehouse.util.AlertUtil;
import lk.ijse.vishmibakehouse.util.SecurityContext;
import lk.ijse.vishmibakehouse.util.ValidationUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class OutletSalesController {

    @FXML
    private ComboBox<ProductDTO> cmbProduct;

    @FXML
    private Label lblAvailableStock;

    @FXML
    private TextField txtUnitPrice;

    @FXML
    private TextField txtQuantity;

    @FXML
    private TableView<SaleItemDTO> tblCart;

    @FXML
    private TableColumn<SaleItemDTO, String> colCartProduct;
    @FXML
    private TableColumn<SaleItemDTO, BigDecimal> colCartQty;
    @FXML
    private TableColumn<SaleItemDTO, BigDecimal> colCartPrice;
    @FXML
    private TableColumn<SaleItemDTO, BigDecimal> colCartTotal;

    @FXML
    private ComboBox<EmployeeDTO> cmbCashier;

    @FXML
    private Label lblGrandTotal;

    @FXML
    private TextField txtCashReceived;

    @FXML
    private Label lblChange;

    private final ProductBO productBO = BOFactory.getInstance().getBO(BOTypes.PRODUCT);
    private final SaleBO saleBO = BOFactory.getInstance().getBO(BOTypes.SALE);
    private final EmployeeBO employeeBO = BOFactory.getInstance().getBO(BOTypes.EMPLOYEE);

    private final ObservableList<SaleItemDTO> cartItems = FXCollections.observableArrayList();
    private final DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");
    private BigDecimal currentNetTotal = BigDecimal.ZERO;

    @FXML
    public void initialize() {
        colCartProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colCartQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colCartPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colCartTotal.setCellValueFactory(new PropertyValueFactory<>("lineTotal"));

        tblCart.setItems(cartItems);

        loadProducts();
        loadCashiers();

        cmbProduct.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtUnitPrice.setText(newVal.getSellingPrice().toString());
                updateAvailableStock(newVal);
            } else {
                txtUnitPrice.clear();
                lblAvailableStock.setText("--");
                lblAvailableStock.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E4D37;");
            }
        });

        txtCashReceived.textProperty().addListener((obs, oldVal, newVal) -> calculateChange());
    }

    private BigDecimal getAvailableStock(long productId) {
        try {
            List<ProductAvailabilityDTO> availList = productBO.getProductAvailability();
            for (ProductAvailabilityDTO p : availList) {
                if (p.getProductId() == productId) {
                    return p.getAvailableQty() != null ? p.getAvailableQty() : BigDecimal.ZERO;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    private void updateAvailableStock(ProductDTO prod) {
        if (prod == null) {
            lblAvailableStock.setText("--");
            lblAvailableStock.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E4D37;");
            return;
        }
        BigDecimal avail = getAvailableStock(prod.getProductId());
        if (avail.compareTo(BigDecimal.ZERO) <= 0) {
            lblAvailableStock.setText("0 " + prod.getUnit() + " (Out of Stock)");
            lblAvailableStock.setStyle("-fx-font-weight: bold; -fx-text-fill: #DC2626;");
        } else {
            lblAvailableStock.setText(avail + " " + prod.getUnit() + " (Available)");
            lblAvailableStock.setStyle("-fx-font-weight: bold; -fx-text-fill: #166534;");
        }
    }

    private void loadProducts() {
        try {
            List<ProductDTO> products = productBO.getActiveProducts();
            cmbProduct.setItems(FXCollections.observableArrayList(products));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadCashiers() {
        try {
            List<EmployeeDTO> employees = employeeBO.getActiveEmployees();
            cmbCashier.setItems(FXCollections.observableArrayList(employees));
            cmbCashier.setConverter(new StringConverter<>() {
                @Override public String toString(EmployeeDTO object) { return object != null ? object.getName() + " (" + object.getJobRole() + ")" : ""; }
                @Override public EmployeeDTO fromString(String string) { return null; }
            });

            // Auto-select currently logged-in user's employee if matching
            if (SecurityContext.getCurrentUser() != null) {
                long currentEmpId = SecurityContext.getCurrentUser().getEmployeeId();
                for (EmployeeDTO e : employees) {
                    if (e.getEmployeeId() == currentEmpId) {
                        cmbCashier.getSelectionModel().select(e);
                        break;
                    }
                }
            }
            if (cmbCashier.getSelectionModel().isEmpty() && !employees.isEmpty()) {
                cmbCashier.getSelectionModel().selectFirst();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleAddToCart(ActionEvent event) {
        ProductDTO prod = cmbProduct.getValue();
        if (prod == null) {
            AlertUtil.showWarning("Product Required", "Please select a bakery product.");
            return;
        }
        if (!ValidationUtil.isPositiveDecimal(txtQuantity.getText())) {
            AlertUtil.showWarning("Quantity Required", "Please enter a valid positive quantity.");
            return;
        }

        BigDecimal qty = new BigDecimal(txtQuantity.getText().trim());

        // 1. Strict Stock Check: Prevent adding out of stock products to cart
        BigDecimal available = getAvailableStock(prod.getProductId());
        if (available.compareTo(BigDecimal.ZERO) <= 0) {
            AlertUtil.showError("Out of Stock",
                    "Product '" + prod.getName() + "' is OUT OF STOCK!\n" +
                    "Cannot add to cart. Please bake or produce new batches first.");
            return;
        }

        BigDecimal currentInCart = BigDecimal.ZERO;
        for (SaleItemDTO item : cartItems) {
            if (item.getProductId() == prod.getProductId()) {
                currentInCart = item.getQuantity();
                break;
            }
        }

        BigDecimal totalRequested = currentInCart.add(qty);
        if (totalRequested.compareTo(available) > 0) {
            BigDecimal maxAddable = available.subtract(currentInCart);
            if (maxAddable.compareTo(BigDecimal.ZERO) <= 0) {
                AlertUtil.showError("Insufficient Stock",
                        "Cart already contains all available stock (" + available + " " + prod.getUnit() + ") of '" + prod.getName() + "'.");
            } else {
                AlertUtil.showError("Insufficient Stock",
                        "Cannot add " + qty + " " + prod.getUnit() + " of '" + prod.getName() + "'.\n" +
                        "Available in stock: " + available + " " + prod.getUnit() + "\n" +
                        "Already in cart: " + currentInCart + " " + prod.getUnit() + "\n" +
                        "Maximum you can still add: " + maxAddable + " " + prod.getUnit());
            }
            return;
        }

        BigDecimal price = prod.getSellingPrice();
        BigDecimal lineTotal = qty.multiply(price);

        // Check if item already exists in cart
        boolean exists = false;
        for (SaleItemDTO item : cartItems) {
            if (item.getProductId() == prod.getProductId()) {
                item.setQuantity(item.getQuantity().add(qty));
                item.setLineTotal(item.getQuantity().multiply(price));
                exists = true;
                break;
            }
        }

        if (!exists) {
            cartItems.add(new SaleItemDTO(0, 0, prod.getProductId(), prod.getName(), qty, price, lineTotal));
        }

        tblCart.refresh();
        recalculateCart();

        txtQuantity.clear();
        cmbProduct.getSelectionModel().clearSelection();
    }

    @FXML
    public void handleRemoveItem(ActionEvent event) {
        SaleItemDTO selected = tblCart.getSelectionModel().getSelectedItem();
        if (selected != null) {
            cartItems.remove(selected);
            recalculateCart();
        } else {
            AlertUtil.showWarning("Selection Required", "Please select an item from cart to remove.");
        }
    }

    private void recalculateCart() {
        currentNetTotal = BigDecimal.ZERO;
        for (SaleItemDTO item : cartItems) {
            currentNetTotal = currentNetTotal.add(item.getLineTotal());
        }
        lblGrandTotal.setText("LKR " + currencyFormat.format(currentNetTotal));
        calculateChange();
    }

    private void calculateChange() {
        if (ValidationUtil.isNonNegativeDecimal(txtCashReceived.getText())) {
            BigDecimal cash = new BigDecimal(txtCashReceived.getText().trim());
            BigDecimal change = cash.subtract(currentNetTotal);
            lblChange.setText("LKR " + currencyFormat.format(change));
            if (change.compareTo(BigDecimal.ZERO) < 0) {
                lblChange.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #DC2626;");
            } else {
                lblChange.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #276247;");
            }
        } else {
            lblChange.setText("LKR 0.00");
        }
    }

    @FXML
    public void handleCompleteSale(ActionEvent event) {
        if (cartItems.isEmpty()) {
            AlertUtil.showWarning("Empty Cart", "Cannot complete a sale with no items.");
            return;
        }
        if (cmbCashier.getValue() == null) {
            AlertUtil.showWarning("Cashier Required", "Please select the cashier staff member.");
            return;
        }
        if (!ValidationUtil.isNonNegativeDecimal(txtCashReceived.getText())) {
            AlertUtil.showWarning("Cash Required", "Please enter the cash received amount.");
            return;
        }

        BigDecimal cash = new BigDecimal(txtCashReceived.getText().trim());
        if (cash.compareTo(currentNetTotal) < 0) {
            AlertUtil.showWarning("Insufficient Cash", "Cash received is less than the total sale amount.");
            return;
        }

        // Live double-check: ensure none of the cart items exceed current available stock
        for (SaleItemDTO item : cartItems) {
            BigDecimal liveAvail = getAvailableStock(item.getProductId());
            if (item.getQuantity().compareTo(liveAvail) > 0) {
                AlertUtil.showError("Transaction Blocked: Insufficient Stock",
                        "Cannot complete sale for '" + item.getProductName() + "'.\n" +
                        "Requested in cart: " + item.getQuantity() + "\n" +
                        "Live available stock: " + liveAvail + "\n" +
                        "Please adjust cart quantity before proceeding.");
                return;
            }
        }

        try {
            SaleDTO sale = new SaleDTO();
            sale.setCashierEmployeeId(cmbCashier.getValue().getEmployeeId());
            sale.setSoldAt(new Timestamp(System.currentTimeMillis()));
            sale.setCashReceived(cash);
            sale.setTotalAmount(currentNetTotal);
            sale.setChangeAmount(cash.subtract(currentNetTotal));

            for (SaleItemDTO item : cartItems) {
                sale.getItems().add(new SaleItemDTO(
                        0, 0, item.getProductId(), item.getProductName(), item.getQuantity(), item.getUnitPrice(), item.getLineTotal()
                ));
            }

            if (saleBO.placeSale(sale)) {
                BigDecimal change = cash.subtract(currentNetTotal);
                AlertUtil.showInfo("Sale Completed Successfully",
                        "Receipt #ORD-" + sale.getSaleId() + "\n" +
                        "Total: LKR " + currencyFormat.format(currentNetTotal) + "\n" +
                        "Cash Received: LKR " + currencyFormat.format(cash) + "\n" +
                        "Change: LKR " + currencyFormat.format(change) + "\n\n" +
                        "Stock has been updated automatically.");
                handleClearOrder(null);
                loadProducts();
            } else {
                AlertUtil.showError("Transaction Failed", "Could not complete the sale transaction.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Database Transaction Error", e.getMessage());
        }
    }

    @FXML
    public void handleClearOrder(ActionEvent event) {
        cartItems.clear();
        recalculateCart();
        txtQuantity.clear();
        txtCashReceived.clear();
        lblChange.setText("LKR 0.00");
        cmbProduct.getSelectionModel().clearSelection();
        lblAvailableStock.setText("--");
        lblAvailableStock.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E4D37;");
    }
}
