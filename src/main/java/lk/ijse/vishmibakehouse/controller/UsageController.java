package lk.ijse.vishmibakehouse.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import lk.ijse.vishmibakehouse.bo.BOFactory;
import lk.ijse.vishmibakehouse.bo.BOTypes;
import lk.ijse.vishmibakehouse.bo.custom.EmployeeBO;
import lk.ijse.vishmibakehouse.bo.custom.IngredientBO;
import lk.ijse.vishmibakehouse.bo.custom.ProductionBO;
import lk.ijse.vishmibakehouse.dto.*;
import lk.ijse.vishmibakehouse.util.AlertUtil;
import lk.ijse.vishmibakehouse.util.ValidationUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class UsageController {

    @FXML
    private ComboBox<ProductionBatchDTO> cmbBatch;

    @FXML
    private ComboBox<IngredientDTO> cmbIngredient;

    @FXML
    private Label lblAvailableStock;

    @FXML
    private TextField txtQty;

    @FXML
    private ComboBox<EmployeeDTO> cmbEmployee;

    @FXML
    private TableView<IngredientUsageDTO> tblUsages;

    @FXML
    private TableColumn<IngredientUsageDTO, Long> colId;
    @FXML
    private TableColumn<IngredientUsageDTO, Long> colBatch;
    @FXML
    private TableColumn<IngredientUsageDTO, String> colProduct;
    @FXML
    private TableColumn<IngredientUsageDTO, String> colIngredient;
    @FXML
    private TableColumn<IngredientUsageDTO, BigDecimal> colQty;
    @FXML
    private TableColumn<IngredientUsageDTO, Timestamp> colDate;
    @FXML
    private TableColumn<IngredientUsageDTO, String> colStaff;

    private final ProductionBO productionBO = BOFactory.getInstance().getBO(BOTypes.PRODUCTION);
    private final IngredientBO ingredientBO = BOFactory.getInstance().getBO(BOTypes.INGREDIENT);
    private final EmployeeBO employeeBO = BOFactory.getInstance().getBO(BOTypes.EMPLOYEE);

    private BigDecimal currentStockAvailable = BigDecimal.ZERO;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("ingredientUsageId"));
        colBatch.setCellValueFactory(new PropertyValueFactory<>("productionBatchId"));
        colProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colIngredient.setCellValueFactory(new PropertyValueFactory<>("ingredientName"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("usedAt"));
        colStaff.setCellValueFactory(new PropertyValueFactory<>("employeeName"));

        loadCombos();
        loadUsageTable();

        cmbIngredient.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateStockAvailability(newVal.getIngredientId());
            } else {
                lblAvailableStock.setText("--");
                currentStockAvailable = BigDecimal.ZERO;
            }
        });
    }

    private void updateStockAvailability(long ingredientId) {
        try {
            List<IngredientStockDTO> list = ingredientBO.getIngredientStockList();
            for (IngredientStockDTO stock : list) {
                if (stock.getIngredientId() == ingredientId) {
                    currentStockAvailable = stock.getAvailableQty();
                    lblAvailableStock.setText(stock.getAvailableQty() + " " + stock.getUnit());
                    return;
                }
            }
            lblAvailableStock.setText("0.000");
            currentStockAvailable = BigDecimal.ZERO;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadCombos() {
        try {
            List<ProductionBatchDTO> batches = productionBO.getAllBatches();
            cmbBatch.setItems(FXCollections.observableArrayList(batches));

            List<IngredientDTO> ingredients = ingredientBO.getActiveIngredients();
            cmbIngredient.setItems(FXCollections.observableArrayList(ingredients));

            List<EmployeeDTO> employees = employeeBO.getActiveEmployees();
            cmbEmployee.setItems(FXCollections.observableArrayList(employees));
            cmbEmployee.setConverter(new StringConverter<>() {
                @Override public String toString(EmployeeDTO object) { return object != null ? object.getName() : ""; }
                @Override public EmployeeDTO fromString(String string) { return null; }
            });
            if (!employees.isEmpty()) cmbEmployee.getSelectionModel().selectFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadUsageTable() {
        try {
            List<IngredientUsageDTO> list = productionBO.getAllUsages();
            tblUsages.setItems(FXCollections.observableArrayList(list));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSave(ActionEvent event) {
        if (cmbBatch.getValue() == null) {
            AlertUtil.showWarning("Validation Error", "Please select a production batch.");
            return;
        }
        if (cmbIngredient.getValue() == null) {
            AlertUtil.showWarning("Validation Error", "Please select an ingredient.");
            return;
        }
        if (!ValidationUtil.isPositiveDecimal(txtQty.getText())) {
            AlertUtil.showWarning("Validation Error", "Please enter a valid positive quantity used.");
            return;
        }
        if (cmbEmployee.getValue() == null) {
            AlertUtil.showWarning("Validation Error", "Please select the recording staff member.");
            return;
        }

        BigDecimal qtyToUse = new BigDecimal(txtQty.getText().trim());
        if (qtyToUse.compareTo(currentStockAvailable) > 0) {
            AlertUtil.showError("Insufficient Stock", "Cannot record usage of " + qtyToUse + ". Only " + currentStockAvailable + " is available in stock.");
            return;
        }

        try {
            IngredientUsageDTO dto = new IngredientUsageDTO(
                    0,
                    cmbBatch.getValue().getProductionBatchId(),
                    "",
                    cmbIngredient.getValue().getIngredientId(),
                    "",
                    cmbEmployee.getValue().getEmployeeId(),
                    "",
                    new Timestamp(System.currentTimeMillis()),
                    qtyToUse
            );

            if (productionBO.recordIngredientUsage(dto)) {
                AlertUtil.showInfo("Success", "Ingredient usage logged and deducted from stock.");
                handleClear(null);
                loadUsageTable();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Database Error", e.getMessage());
        }
    }

    @FXML
    public void handleClear(ActionEvent event) {
        cmbBatch.getSelectionModel().clearSelection();
        cmbIngredient.getSelectionModel().clearSelection();
        lblAvailableStock.setText("--");
        txtQty.clear();
    }
}
