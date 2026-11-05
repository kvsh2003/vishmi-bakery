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
import lk.ijse.vishmibakehouse.dto.EmployeeDTO;
import lk.ijse.vishmibakehouse.dto.IngredientDTO;
import lk.ijse.vishmibakehouse.dto.IngredientPurchaseDTO;
import lk.ijse.vishmibakehouse.util.AlertUtil;
import lk.ijse.vishmibakehouse.util.ValidationUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.List;

public class PurchasesController {

    @FXML
    private ComboBox<IngredientDTO> cmbIngredient;

    @FXML
    private TextField txtQty;

    @FXML
    private TextField txtUnitCost;

    @FXML
    private TextField txtSupplier;

    @FXML
    private ComboBox<EmployeeDTO> cmbEmployee;

    @FXML
    private Label lblTotalCost;

    @FXML
    private TableView<IngredientPurchaseDTO> tblPurchases;

    @FXML
    private TableColumn<IngredientPurchaseDTO, Long> colId;
    @FXML
    private TableColumn<IngredientPurchaseDTO, String> colIngredient;
    @FXML
    private TableColumn<IngredientPurchaseDTO, Timestamp> colDate;
    @FXML
    private TableColumn<IngredientPurchaseDTO, BigDecimal> colQty;
    @FXML
    private TableColumn<IngredientPurchaseDTO, BigDecimal> colUnitCost;
    @FXML
    private TableColumn<IngredientPurchaseDTO, BigDecimal> colTotalCost;
    @FXML
    private TableColumn<IngredientPurchaseDTO, String> colSupplier;
    @FXML
    private TableColumn<IngredientPurchaseDTO, String> colStaff;

    private final IngredientBO ingredientBO = BOFactory.getInstance().getBO(BOTypes.INGREDIENT);
    private final EmployeeBO employeeBO = BOFactory.getInstance().getBO(BOTypes.EMPLOYEE);
    private final DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("ingredientPurchaseId"));
        colIngredient.setCellValueFactory(new PropertyValueFactory<>("ingredientName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("purchasedAt"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colUnitCost.setCellValueFactory(new PropertyValueFactory<>("unitCost"));
        colTotalCost.setCellValueFactory(new PropertyValueFactory<>("totalCost"));
        colSupplier.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        colStaff.setCellValueFactory(new PropertyValueFactory<>("employeeName"));

        loadCombos();
        loadPurchasesTable();

        txtQty.textProperty().addListener((obs, oldVal, newVal) -> calculateTotal());
        txtUnitCost.textProperty().addListener((obs, oldVal, newVal) -> calculateTotal());
    }

    private void calculateTotal() {
        if (ValidationUtil.isPositiveDecimal(txtQty.getText()) && ValidationUtil.isNonNegativeDecimal(txtUnitCost.getText())) {
            BigDecimal qty = new BigDecimal(txtQty.getText().trim());
            BigDecimal cost = new BigDecimal(txtUnitCost.getText().trim());
            lblTotalCost.setText("LKR " + currencyFormat.format(qty.multiply(cost)));
        } else {
            lblTotalCost.setText("LKR 0.00");
        }
    }

    private void loadCombos() {
        try {
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

    private void loadPurchasesTable() {
        try {
            List<IngredientPurchaseDTO> list = ingredientBO.getAllPurchases();
            tblPurchases.setItems(FXCollections.observableArrayList(list));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSave(ActionEvent event) {
        if (cmbIngredient.getValue() == null) {
            AlertUtil.showWarning("Validation Error", "Please select an ingredient.");
            return;
        }
        if (!ValidationUtil.isPositiveDecimal(txtQty.getText())) {
            AlertUtil.showWarning("Validation Error", "Please enter a valid positive quantity.");
            return;
        }
        if (!ValidationUtil.isNonNegativeDecimal(txtUnitCost.getText())) {
            AlertUtil.showWarning("Validation Error", "Please enter a valid non-negative unit cost.");
            return;
        }
        if (cmbEmployee.getValue() == null) {
            AlertUtil.showWarning("Validation Error", "Please select the employee who received the goods.");
            return;
        }

        try {
            IngredientPurchaseDTO dto = new IngredientPurchaseDTO(
                    0,
                    cmbIngredient.getValue().getIngredientId(),
                    "",
                    cmbEmployee.getValue().getEmployeeId(),
                    "",
                    new Timestamp(System.currentTimeMillis()),
                    new BigDecimal(txtQty.getText().trim()),
                    new BigDecimal(txtUnitCost.getText().trim()),
                    txtSupplier.getText() != null ? txtSupplier.getText().trim() : "",
                    BigDecimal.ZERO
            );

            if (ingredientBO.recordPurchase(dto)) {
                AlertUtil.showInfo("Success", "Ingredient purchase recorded and stock balance incremented.");
                handleClear(null);
                loadPurchasesTable();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Database Error", e.getMessage());
        }
    }

    @FXML
    public void handleClear(ActionEvent event) {
        cmbIngredient.getSelectionModel().clearSelection();
        txtQty.clear();
        txtUnitCost.clear();
        txtSupplier.clear();
        lblTotalCost.setText("LKR 0.00");
    }
}
