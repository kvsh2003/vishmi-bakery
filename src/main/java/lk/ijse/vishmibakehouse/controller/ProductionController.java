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
import lk.ijse.vishmibakehouse.bo.custom.ProductBO;
import lk.ijse.vishmibakehouse.bo.custom.ProductionBO;
import lk.ijse.vishmibakehouse.dto.EmployeeDTO;
import lk.ijse.vishmibakehouse.dto.ProductDTO;
import lk.ijse.vishmibakehouse.dto.ProductionBatchDTO;
import lk.ijse.vishmibakehouse.util.AlertUtil;
import lk.ijse.vishmibakehouse.util.ValidationUtil;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ProductionController {

    @FXML
    private DatePicker dtProducedOn;

    @FXML
    private ComboBox<ProductDTO> cmbProduct;

    @FXML
    private ComboBox<EmployeeDTO> cmbEmployee;

    @FXML
    private TextField txtPlannedQty;

    @FXML
    private TextField txtActualQty;

    @FXML
    private ComboBox<String> cmbStatus;

    @FXML
    private TableView<ProductionBatchDTO> tblBatches;

    @FXML
    private TableColumn<ProductionBatchDTO, Long> colBatchId;
    @FXML
    private TableColumn<ProductionBatchDTO, Date> colDate;
    @FXML
    private TableColumn<ProductionBatchDTO, String> colProduct;
    @FXML
    private TableColumn<ProductionBatchDTO, String> colEmployee;
    @FXML
    private TableColumn<ProductionBatchDTO, BigDecimal> colPlanned;
    @FXML
    private TableColumn<ProductionBatchDTO, BigDecimal> colActual;
    @FXML
    private TableColumn<ProductionBatchDTO, String> colStatus;

    private final ProductionBO productionBO = BOFactory.getInstance().getBO(BOTypes.PRODUCTION);
    private final ProductBO productBO = BOFactory.getInstance().getBO(BOTypes.PRODUCT);
    private final EmployeeBO employeeBO = BOFactory.getInstance().getBO(BOTypes.EMPLOYEE);

    private long selectedBatchId = 0;

    @FXML
    public void initialize() {
        dtProducedOn.setValue(LocalDate.now());
        cmbStatus.setItems(FXCollections.observableArrayList("PLANNED", "IN_PROGRESS", "COMPLETED", "CANCELLED"));
        cmbStatus.getSelectionModel().selectFirst();

        colBatchId.setCellValueFactory(new PropertyValueFactory<>("productionBatchId"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("producedOn"));
        colProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colEmployee.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        colPlanned.setCellValueFactory(new PropertyValueFactory<>("plannedQty"));
        colActual.setCellValueFactory(new PropertyValueFactory<>("actualQty"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadCombos();
        loadTableData();

        tblBatches.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedBatchId = newVal.getProductionBatchId();
                if (newVal.getProducedOn() != null) {
                    dtProducedOn.setValue(newVal.getProducedOn().toLocalDate());
                }
                txtPlannedQty.setText(newVal.getPlannedQty().toString());
                txtActualQty.setText(newVal.getActualQty() != null ? newVal.getActualQty().toString() : "0");
                cmbStatus.getSelectionModel().select(newVal.getStatus());

                for (ProductDTO p : cmbProduct.getItems()) {
                    if (p.getProductId() == newVal.getProductId()) {
                        cmbProduct.getSelectionModel().select(p);
                        break;
                    }
                }
                for (EmployeeDTO e : cmbEmployee.getItems()) {
                    if (e.getEmployeeId() == newVal.getRecordedByEmployeeId()) {
                        cmbEmployee.getSelectionModel().select(e);
                        break;
                    }
                }
            }
        });
    }

    private void loadCombos() {
        try {
            List<ProductDTO> products = productBO.getActiveProducts();
            cmbProduct.setItems(FXCollections.observableArrayList(products));

            List<EmployeeDTO> employees = employeeBO.getActiveEmployees();
            cmbEmployee.setItems(FXCollections.observableArrayList(employees));
            cmbEmployee.setConverter(new StringConverter<>() {
                @Override public String toString(EmployeeDTO object) { return object != null ? object.getName() + " (" + object.getJobRole() + ")" : ""; }
                @Override public EmployeeDTO fromString(String string) { return null; }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadTableData() {
        try {
            List<ProductionBatchDTO> list = productionBO.getAllBatches();
            tblBatches.setItems(FXCollections.observableArrayList(list));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSave(ActionEvent event) {
        if (!validateForm()) return;

        try {
            BigDecimal actual = (txtActualQty.getText() != null && !txtActualQty.getText().trim().isEmpty())
                    ? new BigDecimal(txtActualQty.getText().trim()) : BigDecimal.ZERO;

            ProductionBatchDTO dto = new ProductionBatchDTO(
                    0,
                    cmbProduct.getValue().getProductId(),
                    "",
                    cmbEmployee.getValue().getEmployeeId(),
                    "",
                    Date.valueOf(dtProducedOn.getValue()),
                    new BigDecimal(txtPlannedQty.getText().trim()),
                    actual,
                    cmbStatus.getValue()
            );

            long batchId = productionBO.createProductionBatch(dto);
            if (batchId > 0) {
                AlertUtil.showInfo("Success", "Production Batch #" + batchId + " logged successfully.");
                handleClear(null);
                loadTableData();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Database Error", e.getMessage());
        }
    }

    @FXML
    public void handleUpdate(ActionEvent event) {
        if (selectedBatchId <= 0) {
            AlertUtil.showWarning("Selection Required", "Please select a production batch to update.");
            return;
        }
        if (!validateForm()) return;

        try {
            BigDecimal actual = (txtActualQty.getText() != null && !txtActualQty.getText().trim().isEmpty())
                    ? new BigDecimal(txtActualQty.getText().trim()) : BigDecimal.ZERO;

            ProductionBatchDTO dto = new ProductionBatchDTO(
                    selectedBatchId,
                    cmbProduct.getValue().getProductId(),
                    "",
                    cmbEmployee.getValue().getEmployeeId(),
                    "",
                    Date.valueOf(dtProducedOn.getValue()),
                    new BigDecimal(txtPlannedQty.getText().trim()),
                    actual,
                    cmbStatus.getValue()
            );

            if (productionBO.updateProductionBatch(dto)) {
                AlertUtil.showInfo("Success", "Production Batch #" + selectedBatchId + " updated successfully.");
                handleClear(null);
                loadTableData();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Database Error", e.getMessage());
        }
    }

    @FXML
    public void handleComplete(ActionEvent event) {
        if (selectedBatchId <= 0) {
            AlertUtil.showWarning("Selection Required", "Please select a production batch to mark as COMPLETED.");
            return;
        }

        String actualStr = txtActualQty.getText() != null ? txtActualQty.getText().trim() : "";
        if (!ValidationUtil.isPositiveDecimal(actualStr)) {
            AlertUtil.showWarning("Actual Quantity Required", "Please enter a valid positive actual finished quantity before completing the batch.");
            return;
        }

        try {
            BigDecimal actual = new BigDecimal(actualStr);
            if (productionBO.updateBatchStatus(selectedBatchId, "COMPLETED", actual)) {
                AlertUtil.showInfo("Batch Completed", "Production Batch #" + selectedBatchId + " marked as COMPLETED.");
                handleClear(null);
                loadTableData();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Database Error", e.getMessage());
        }
    }

    @FXML
    public void handleClear(ActionEvent event) {
        selectedBatchId = 0;
        dtProducedOn.setValue(LocalDate.now());
        cmbProduct.getSelectionModel().clearSelection();
        cmbEmployee.getSelectionModel().clearSelection();
        txtPlannedQty.clear();
        txtActualQty.clear();
        cmbStatus.getSelectionModel().selectFirst();
        tblBatches.getSelectionModel().clearSelection();
    }

    private boolean validateForm() {
        if (dtProducedOn.getValue() == null) {
            AlertUtil.showWarning("Validation Error", "Please select a production date.");
            return false;
        }
        if (cmbProduct.getValue() == null) {
            AlertUtil.showWarning("Validation Error", "Please select a bakery product.");
            return false;
        }
        if (cmbEmployee.getValue() == null) {
            AlertUtil.showWarning("Validation Error", "Please select the responsible staff member.");
            return false;
        }
        if (!ValidationUtil.isPositiveDecimal(txtPlannedQty.getText())) {
            AlertUtil.showWarning("Validation Error", "Planned quantity must be a positive number.");
            return false;
        }
        return true;
    }
}
