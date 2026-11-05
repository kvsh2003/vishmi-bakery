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
import lk.ijse.vishmibakehouse.bo.custom.LeftoverBO;
import lk.ijse.vishmibakehouse.bo.custom.ProductBO;
import lk.ijse.vishmibakehouse.bo.custom.ProductionBO;
import lk.ijse.vishmibakehouse.dto.*;
import lk.ijse.vishmibakehouse.util.AlertUtil;
import lk.ijse.vishmibakehouse.util.ValidationUtil;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

public class LeftoversController {

    @FXML
    private ComboBox<ProductionBatchDTO> cmbSourceBatch;

    @FXML
    private DatePicker dtLeftoverDate;

    @FXML
    private TextField txtLeftoverPieces;

    @FXML
    private ComboBox<EmployeeDTO> cmbLeftoverStaff;

    @FXML
    private ComboBox<LeftoverBalanceDTO> cmbLeftoverLot;

    @FXML
    private TextField txtPiecesToConsume;

    @FXML
    private TextField txtOutputKg;

    @FXML
    private ComboBox<ProductDTO> cmbOutputProduct;

    @FXML
    private TableView<LeftoverBalanceDTO> tblLeftoverBalances;

    @FXML
    private TableColumn<LeftoverBalanceDTO, Long> colLotId;
    @FXML
    private TableColumn<LeftoverBalanceDTO, Long> colLotBatch;
    @FXML
    private TableColumn<LeftoverBalanceDTO, Date> colLotDate;
    @FXML
    private TableColumn<LeftoverBalanceDTO, Integer> colLotInitial;
    @FXML
    private TableColumn<LeftoverBalanceDTO, Integer> colLotUsed;
    @FXML
    private TableColumn<LeftoverBalanceDTO, Integer> colLotRemaining;

    private final LeftoverBO leftoverBO = BOFactory.getInstance().getBO(BOTypes.LEFTOVER);
    private final ProductionBO productionBO = BOFactory.getInstance().getBO(BOTypes.PRODUCTION);
    private final ProductBO productBO = BOFactory.getInstance().getBO(BOTypes.PRODUCT);
    private final EmployeeBO employeeBO = BOFactory.getInstance().getBO(BOTypes.EMPLOYEE);

    @FXML
    public void initialize() {
        dtLeftoverDate.setValue(LocalDate.now());

        colLotId.setCellValueFactory(new PropertyValueFactory<>("leftoverBatchId"));
        colLotBatch.setCellValueFactory(new PropertyValueFactory<>("productionBatchId"));
        colLotDate.setCellValueFactory(new PropertyValueFactory<>("recordedOn"));
        colLotInitial.setCellValueFactory(new PropertyValueFactory<>("qtyPieces"));
        colLotUsed.setCellValueFactory(new PropertyValueFactory<>("usedPieces"));
        colLotRemaining.setCellValueFactory(new PropertyValueFactory<>("remainingPieces"));

        loadCombos();
        loadBalancesTable();
    }

    private void loadCombos() {
        try {
            List<ProductionBatchDTO> batches = productionBO.getAllBatches();
            cmbSourceBatch.setItems(FXCollections.observableArrayList(batches));

            List<EmployeeDTO> employees = employeeBO.getActiveEmployees();
            cmbLeftoverStaff.setItems(FXCollections.observableArrayList(employees));
            cmbLeftoverStaff.setConverter(new StringConverter<>() {
                @Override public String toString(EmployeeDTO object) { return object != null ? object.getName() : ""; }
                @Override public EmployeeDTO fromString(String string) { return null; }
            });
            if (!employees.isEmpty()) cmbLeftoverStaff.getSelectionModel().selectFirst();

            List<ProductDTO> products = productBO.getActiveProducts();
            cmbOutputProduct.setItems(FXCollections.observableArrayList(products));
            // Select bread crumb product if found
            for (ProductDTO p : products) {
                if (p.getName().toLowerCase().contains("crumb")) {
                    cmbOutputProduct.getSelectionModel().select(p);
                    break;
                }
            }

            List<LeftoverBalanceDTO> balances = leftoverBO.getLeftoverBalances();
            cmbLeftoverLot.setItems(FXCollections.observableArrayList(balances));
            cmbLeftoverLot.setConverter(new StringConverter<>() {
                @Override public String toString(LeftoverBalanceDTO object) {
                    return object != null ? "Lot #" + object.getLeftoverBatchId() + " (" + object.getRemainingPieces() + " pcs remaining)" : "";
                }
                @Override public LeftoverBalanceDTO fromString(String string) { return null; }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadBalancesTable() {
        try {
            List<LeftoverBalanceDTO> balances = leftoverBO.getLeftoverBalances();
            tblLeftoverBalances.setItems(FXCollections.observableArrayList(balances));
            cmbLeftoverLot.setItems(FXCollections.observableArrayList(balances));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSaveLeftover(ActionEvent event) {
        if (cmbSourceBatch.getValue() == null) {
            AlertUtil.showWarning("Batch Required", "Please select the production batch that had unsold leftovers.");
            return;
        }
        if (!ValidationUtil.isPositiveInteger(txtLeftoverPieces.getText())) {
            AlertUtil.showWarning("Pieces Required", "Please enter a valid positive number of leftover pieces.");
            return;
        }
        if (cmbLeftoverStaff.getValue() == null) {
            AlertUtil.showWarning("Staff Required", "Please select the responsible staff member.");
            return;
        }

        try {
            LeftoverBatchDTO dto = new LeftoverBatchDTO(
                    0,
                    cmbSourceBatch.getValue().getProductionBatchId(),
                    "",
                    cmbLeftoverStaff.getValue().getEmployeeId(),
                    "",
                    Date.valueOf(dtLeftoverDate.getValue()),
                    Integer.parseInt(txtLeftoverPieces.getText().trim())
            );

            if (leftoverBO.recordLeftoverBatch(dto)) {
                AlertUtil.showInfo("Leftover Recorded", "Leftover lot logged successfully. Ready for bread-crumb reuse.");
                txtLeftoverPieces.clear();
                cmbSourceBatch.getSelectionModel().clearSelection();
                loadBalancesTable();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Database Error", e.getMessage());
        }
    }

    @FXML
    public void handleProduceCrumbs(ActionEvent event) {
        LeftoverBalanceDTO lot = cmbLeftoverLot.getValue();
        if (lot == null) {
            AlertUtil.showWarning("Lot Required", "Please select an available leftover lot to consume.");
            return;
        }
        if (!ValidationUtil.isPositiveInteger(txtPiecesToConsume.getText())) {
            AlertUtil.showWarning("Pieces Required", "Please enter positive pieces to consume.");
            return;
        }
        if (!ValidationUtil.isPositiveDecimal(txtOutputKg.getText())) {
            AlertUtil.showWarning("Output KG Required", "Please enter valid output bread crumb weight (KG).");
            return;
        }
        if (cmbOutputProduct.getValue() == null) {
            AlertUtil.showWarning("Output Product Required", "Please select the target catalog product (e.g. Bread Crumbs 500g).");
            return;
        }

        int pieces = Integer.parseInt(txtPiecesToConsume.getText().trim());
        if (pieces > lot.getRemainingPieces()) {
            AlertUtil.showError("Excessive Consumption", "Cannot consume " + pieces + " pieces. Lot #" + lot.getLeftoverBatchId() + " only has " + lot.getRemainingPieces() + " remaining.");
            return;
        }

        try {
            long staffId = cmbLeftoverStaff.getValue() != null ? cmbLeftoverStaff.getValue().getEmployeeId() : 3;

            BreadCrumbBatchDTO crumbBatch = new BreadCrumbBatchDTO(
                    0,
                    cmbOutputProduct.getValue().getProductId(),
                    "",
                    staffId,
                    "",
                    new Timestamp(System.currentTimeMillis()),
                    new BigDecimal(txtOutputKg.getText().trim())
            );

            crumbBatch.getInputs().add(new BreadCrumbInputDTO(0, lot.getLeftoverBatchId(), pieces));

            if (leftoverBO.produceBreadCrumbs(crumbBatch)) {
                AlertUtil.showInfo("Bread Crumbs Produced", "Bread crumb batch #" + crumbBatch.getBreadCrumbBatchId() + " processed. Catalog product availability incremented.");
                txtPiecesToConsume.clear();
                txtOutputKg.clear();
                loadBalancesTable();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Processing Error", e.getMessage());
        }
    }
}
