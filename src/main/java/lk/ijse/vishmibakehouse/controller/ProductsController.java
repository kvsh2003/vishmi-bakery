package lk.ijse.vishmibakehouse.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.vishmibakehouse.bo.BOFactory;
import lk.ijse.vishmibakehouse.bo.BOTypes;
import lk.ijse.vishmibakehouse.bo.custom.ProductBO;
import lk.ijse.vishmibakehouse.dto.ProductDTO;
import lk.ijse.vishmibakehouse.util.AlertUtil;
import lk.ijse.vishmibakehouse.util.ValidationUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ProductsController {

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtCategory;

    @FXML
    private ComboBox<String> cmbUnit;

    @FXML
    private TextField txtPrice;

    @FXML
    private CheckBox chkActive;

    @FXML
    private TextField txtSearch;

    @FXML
    private TableView<ProductDTO> tblProducts;

    @FXML
    private TableColumn<ProductDTO, Long> colId;
    @FXML
    private TableColumn<ProductDTO, String> colName;
    @FXML
    private TableColumn<ProductDTO, String> colCategory;
    @FXML
    private TableColumn<ProductDTO, String> colUnit;
    @FXML
    private TableColumn<ProductDTO, BigDecimal> colPrice;
    @FXML
    private TableColumn<ProductDTO, Boolean> colActive;

    private final ProductBO productBO = BOFactory.getInstance().getBO(BOTypes.PRODUCT);
    private long selectedProductId = 0;

    @FXML
    public void initialize() {
        cmbUnit.setItems(FXCollections.observableArrayList("PIECE", "KG"));
        cmbUnit.getSelectionModel().selectFirst();

        colId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));
        colActive.setCellValueFactory(new PropertyValueFactory<>("active"));

        tblProducts.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedProductId = newVal.getProductId();
                txtName.setText(newVal.getName());
                txtCategory.setText(newVal.getCategory());
                cmbUnit.getSelectionModel().select(newVal.getUnit());
                txtPrice.setText(newVal.getSellingPrice().toString());
                chkActive.setSelected(newVal.isActive());
            }
        });

        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            searchProducts(newVal != null ? newVal.trim() : "");
        });

        loadTableData();
    }

    private void loadTableData() {
        try {
            List<ProductDTO> list = productBO.getAllProducts();
            tblProducts.setItems(FXCollections.observableArrayList(list));
        } catch (NumberFormatException e) {
            AlertUtil.showWarning("Invalid Price", "Please enter a valid numeric selling price.");
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Data Error", "Failed to load products: " + e.getMessage());
        }
    }

    private void searchProducts(String keyword) {
        try {
            List<ProductDTO> list = keyword.isEmpty() ? productBO.getAllProducts() : productBO.searchProducts(keyword);
            tblProducts.setItems(FXCollections.observableArrayList(list));
        } catch (NumberFormatException e) {
            AlertUtil.showWarning("Invalid Price", "Please enter a valid numeric selling price.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSave(ActionEvent event) {
        if (!validateForm()) return;

        try {
            ProductDTO dto = new ProductDTO(
                    0,
                    txtName.getText().trim(),
                    txtCategory.getText().trim(),
                    cmbUnit.getValue(),
                    new BigDecimal(txtPrice.getText().trim()),
                    chkActive.isSelected()
            );
            if (productBO.saveProduct(dto)) {
                AlertUtil.showInfo("Success", "Product successfully added to catalog.");
                handleClear(null);
                loadTableData();
            } else {
                AlertUtil.showError("Save Failed", "Could not save the product record.");
            }
        } catch (NumberFormatException e) {
            AlertUtil.showWarning("Invalid Price", "Please enter a valid numeric selling price.");
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Database Error", e.getMessage());
        }
    }

    @FXML
    public void handleUpdate(ActionEvent event) {
        if (selectedProductId <= 0) {
            AlertUtil.showWarning("Selection Required", "Please select a product from the table to update.");
            return;
        }
        if (!validateForm()) return;

        try {
            ProductDTO dto = new ProductDTO(
                    selectedProductId,
                    txtName.getText().trim(),
                    txtCategory.getText().trim(),
                    cmbUnit.getValue(),
                    new BigDecimal(txtPrice.getText().trim()),
                    chkActive.isSelected()
            );
            if (productBO.updateProduct(dto)) {
                AlertUtil.showInfo("Success", "Product details updated successfully.");
                handleClear(null);
                loadTableData();
            } else {
                AlertUtil.showError("Update Failed", "Could not update product details.");
            }
        } catch (NumberFormatException e) {
            AlertUtil.showWarning("Invalid Price", "Please enter a valid numeric selling price.");
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Database Error", e.getMessage());
        }
    }

    @FXML
    public void handleDelete(ActionEvent event) {
        if (selectedProductId <= 0) {
            AlertUtil.showWarning("Selection Required", "Please select a product from the table to deactivate.");
            return;
        }
        if (AlertUtil.showConfirmation("Confirm Deactivation", "Are you sure you want to deactivate this product from catalog?")) {
            try {
                if (productBO.deleteProduct(selectedProductId)) {
                    AlertUtil.showInfo("Success", "Product deactivated successfully.");
                    handleClear(null);
                    loadTableData();
                }
            } catch (NumberFormatException e) {
            AlertUtil.showWarning("Invalid Price", "Please enter a valid numeric selling price.");
        } catch (SQLException e) {
                e.printStackTrace();
                AlertUtil.showError("Database Error", e.getMessage());
            }
        }
    }

    @FXML
    public void handleClear(ActionEvent event) {
        selectedProductId = 0;
        txtName.clear();
        txtCategory.clear();
        cmbUnit.getSelectionModel().selectFirst();
        txtPrice.clear();
        chkActive.setSelected(true);
        tblProducts.getSelectionModel().clearSelection();
    }

    private boolean validateForm() {
        if (txtName.getText() == null || txtName.getText().trim().isEmpty()) {
            AlertUtil.showWarning("Validation Error", "Product Name is required.");
            return false;
        }
        if (txtCategory.getText() == null || txtCategory.getText().trim().isEmpty()) {
            AlertUtil.showWarning("Validation Error", "Category is required.");
            return false;
        }
        if (!ValidationUtil.isPositiveDecimal(txtPrice.getText())) {
            AlertUtil.showWarning("Validation Error", "Please enter a valid positive selling price.");
            return false;
        }
        return true;
    }
}
