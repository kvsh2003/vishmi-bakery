package lk.ijse.vishmibakehouse.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.vishmibakehouse.bo.BOFactory;
import lk.ijse.vishmibakehouse.bo.BOTypes;
import lk.ijse.vishmibakehouse.bo.custom.IngredientBO;
import lk.ijse.vishmibakehouse.dto.IngredientDTO;
import lk.ijse.vishmibakehouse.dto.IngredientStockDTO;
import lk.ijse.vishmibakehouse.util.AlertUtil;
import lk.ijse.vishmibakehouse.util.ValidationUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class IngredientsController {

    @FXML
    private TextField txtName;

    @FXML
    private ComboBox<String> cmbUnit;

    @FXML
    private TextField txtReorderLevel;

    @FXML
    private CheckBox chkActive;

    @FXML
    private TableView<IngredientStockDTO> tblIngredients;

    @FXML
    private TableColumn<IngredientStockDTO, Long> colId;
    @FXML
    private TableColumn<IngredientStockDTO, String> colName;
    @FXML
    private TableColumn<IngredientStockDTO, String> colUnit;
    @FXML
    private TableColumn<IngredientStockDTO, BigDecimal> colPurchased;
    @FXML
    private TableColumn<IngredientStockDTO, BigDecimal> colUsed;
    @FXML
    private TableColumn<IngredientStockDTO, BigDecimal> colAvailable;
    @FXML
    private TableColumn<IngredientStockDTO, BigDecimal> colReorder;
    @FXML
    private TableColumn<IngredientStockDTO, String> colStatus;

    private final IngredientBO ingredientBO = BOFactory.getInstance().getBO(BOTypes.INGREDIENT);
    private long selectedIngredientId = 0;

    @FXML
    public void initialize() {
        cmbUnit.setItems(FXCollections.observableArrayList("KG", "G", "L", "ML", "PIECE"));
        cmbUnit.getSelectionModel().selectFirst();

        colId.setCellValueFactory(new PropertyValueFactory<>("ingredientId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        colPurchased.setCellValueFactory(new PropertyValueFactory<>("purchasedQty"));
        colUsed.setCellValueFactory(new PropertyValueFactory<>("usedQty"));
        colAvailable.setCellValueFactory(new PropertyValueFactory<>("availableQty"));
        colReorder.setCellValueFactory(new PropertyValueFactory<>("reorderLevel"));

        colStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    IngredientStockDTO row = getTableRow().getItem();
                    if (row.isLowStock()) {
                        setText("⚠️ LOW STOCK");
                        setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold;");
                    } else {
                        setText("✓ Sufficient");
                        setStyle("-fx-text-fill: #16A34A; -fx-font-weight: bold;");
                    }
                }
            }
        });

        tblIngredients.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedIngredientId = newVal.getIngredientId();
                txtName.setText(newVal.getName());
                cmbUnit.getSelectionModel().select(newVal.getUnit());
                txtReorderLevel.setText(newVal.getReorderLevel().toString());
                chkActive.setSelected(newVal.isActive());
            }
        });

        loadStockTable();
    }

    private void loadStockTable() {
        try {
            List<IngredientStockDTO> list = ingredientBO.getIngredientStockList();
            tblIngredients.setItems(FXCollections.observableArrayList(list));
        } catch (NumberFormatException e) {
            AlertUtil.showWarning("Invalid Number Format", "Please enter a valid numeric value for reorder level.");
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Database Error", "Failed to load ingredient inventory: " + e.getMessage());
        }
    }

    @FXML
    public void handleSave(ActionEvent event) {
        if (!validateForm()) return;

        try {
            IngredientDTO dto = new IngredientDTO(
                    0,
                    txtName.getText().trim(),
                    cmbUnit.getValue(),
                    new BigDecimal(txtReorderLevel.getText().trim()),
                    chkActive.isSelected()
            );
            if (ingredientBO.saveIngredient(dto)) {
                AlertUtil.showInfo("Success", "Ingredient added to master catalog.");
                handleClear(null);
                loadStockTable();
            }
        } catch (NumberFormatException e) {
            AlertUtil.showWarning("Invalid Number Format", "Please enter a valid numeric value for reorder level.");
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Database Error", e.getMessage());
        }
    }

    @FXML
    public void handleUpdate(ActionEvent event) {
        if (selectedIngredientId <= 0) {
            AlertUtil.showWarning("Selection Required", "Please select an ingredient from the table to update.");
            return;
        }
        if (!validateForm()) return;

        try {
            IngredientDTO dto = new IngredientDTO(
                    selectedIngredientId,
                    txtName.getText().trim(),
                    cmbUnit.getValue(),
                    new BigDecimal(txtReorderLevel.getText().trim()),
                    chkActive.isSelected()
            );
            if (ingredientBO.updateIngredient(dto)) {
                AlertUtil.showInfo("Success", "Ingredient details updated successfully.");
                handleClear(null);
                loadStockTable();
            }
        } catch (NumberFormatException e) {
            AlertUtil.showWarning("Invalid Number Format", "Please enter a valid numeric value for reorder level.");
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Database Error", e.getMessage());
        }
    }

    @FXML
    public void handleDelete(ActionEvent event) {
        if (selectedIngredientId <= 0) {
            AlertUtil.showWarning("Selection Required", "Please select an ingredient to deactivate.");
            return;
        }
        if (AlertUtil.showConfirmation("Confirm Deactivation", "Deactivate this ingredient?")) {
            try {
                if (ingredientBO.deleteIngredient(selectedIngredientId)) {
                    AlertUtil.showInfo("Success", "Ingredient deactivated.");
                    handleClear(null);
                    loadStockTable();
                }
            } catch (NumberFormatException e) {
            AlertUtil.showWarning("Invalid Number Format", "Please enter a valid numeric value for reorder level.");
        } catch (SQLException e) {
                e.printStackTrace();
                AlertUtil.showError("Database Error", e.getMessage());
            }
        }
    }

    @FXML
    public void handleClear(ActionEvent event) {
        selectedIngredientId = 0;
        txtName.clear();
        cmbUnit.getSelectionModel().selectFirst();
        txtReorderLevel.clear();
        chkActive.setSelected(true);
        tblIngredients.getSelectionModel().clearSelection();
    }

    private boolean validateForm() {
        if (txtName.getText() == null || txtName.getText().trim().isEmpty()) {
            AlertUtil.showWarning("Validation Error", "Ingredient Name is required.");
            return false;
        }
        if (!ValidationUtil.isNonNegativeDecimal(txtReorderLevel.getText())) {
            AlertUtil.showWarning("Validation Error", "Reorder level must be a non-negative number.");
            return false;
        }
        return true;
    }
}
