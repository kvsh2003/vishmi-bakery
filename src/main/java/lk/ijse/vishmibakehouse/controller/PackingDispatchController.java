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
import lk.ijse.vishmibakehouse.bo.custom.DeliveryBO;
import lk.ijse.vishmibakehouse.bo.custom.EmployeeBO;
import lk.ijse.vishmibakehouse.bo.custom.ProductBO;
import lk.ijse.vishmibakehouse.dto.*;
import lk.ijse.vishmibakehouse.util.AlertUtil;
import lk.ijse.vishmibakehouse.util.ValidationUtil;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PackingDispatchController {

    @FXML
    private ComboBox<ShopDTO> cmbShop;

    @FXML
    private DatePicker dtDeliveryDate;

    @FXML
    private ComboBox<EmployeeDTO> cmbPackedBy;

    @FXML
    private ComboBox<VehicleDTO> cmbVehicle;

    @FXML
    private ComboBox<EmployeeDTO> cmbDeliveryStaff;

    @FXML
    private ComboBox<ProductDTO> cmbProduct;

    @FXML
    private TextField txtPackedQty;

    @FXML
    private TableView<DeliveryItemDTO> tblPackingItems;

    @FXML
    private TableColumn<DeliveryItemDTO, String> colItemProduct;
    @FXML
    private TableColumn<DeliveryItemDTO, BigDecimal> colItemQty;

    private final DeliveryBO deliveryBO = BOFactory.getInstance().getBO(BOTypes.DELIVERY);
    private final ProductBO productBO = BOFactory.getInstance().getBO(BOTypes.PRODUCT);
    private final EmployeeBO employeeBO = BOFactory.getInstance().getBO(BOTypes.EMPLOYEE);

    private final ObservableList<DeliveryItemDTO> packingList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        dtDeliveryDate.setValue(LocalDate.now());

        colItemProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colItemQty.setCellValueFactory(new PropertyValueFactory<>("packedQty"));

        tblPackingItems.setItems(packingList);

        loadDropdowns();
    }

    private void loadDropdowns() {
        try {
            List<ShopDTO> shops = deliveryBO.getActiveShops();
            cmbShop.setItems(FXCollections.observableArrayList(shops));

            List<VehicleDTO> vehicles = deliveryBO.getActiveVehicles();
            cmbVehicle.setItems(FXCollections.observableArrayList(vehicles));

            List<EmployeeDTO> employees = employeeBO.getActiveEmployees();
            cmbPackedBy.setItems(FXCollections.observableArrayList(employees));
            cmbDeliveryStaff.setItems(FXCollections.observableArrayList(employees));

            StringConverter<EmployeeDTO> empConverter = new StringConverter<>() {
                @Override public String toString(EmployeeDTO object) { return object != null ? object.getName() + " (" + object.getJobRole() + ")" : ""; }
                @Override public EmployeeDTO fromString(String string) { return null; }
            };
            cmbPackedBy.setConverter(empConverter);
            cmbDeliveryStaff.setConverter(empConverter);

            List<ProductDTO> products = productBO.getActiveProducts();
            cmbProduct.setItems(FXCollections.observableArrayList(products));
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    @FXML
    public void handleAddItem(ActionEvent event) {
        ProductDTO prod = cmbProduct.getValue();
        if (prod == null) {
            AlertUtil.showWarning("Product Required", "Please select a product to pack.");
            return;
        }
        if (!ValidationUtil.isPositiveDecimal(txtPackedQty.getText())) {
            AlertUtil.showWarning("Quantity Required", "Please enter a valid positive packed quantity.");
            return;
        }

        BigDecimal qty = new BigDecimal(txtPackedQty.getText().trim());

        // Enforce stock check: prevent packing items that are out of stock
        BigDecimal available = getAvailableStock(prod.getProductId());
        if (available.compareTo(BigDecimal.ZERO) <= 0) {
            AlertUtil.showError("Out of Stock", "Product '" + prod.getName() + "' is OUT OF STOCK! Cannot pack for delivery.");
            return;
        }

        BigDecimal alreadyPacked = BigDecimal.ZERO;
        for (DeliveryItemDTO item : packingList) {
            if (item.getProductId() == prod.getProductId()) {
                alreadyPacked = item.getPackedQty();
                break;
            }
        }

        if (alreadyPacked.add(qty).compareTo(available) > 0) {
            BigDecimal maxCanPack = available.subtract(alreadyPacked);
            AlertUtil.showError("Insufficient Stock",
                    "Cannot pack " + qty + " of '" + prod.getName() + "'.\n" +
                    "Available Stock: " + available + " " + prod.getUnit() + "\n" +
                    (alreadyPacked.compareTo(BigDecimal.ZERO) > 0 ? "Already in crate: " + alreadyPacked + " " + prod.getUnit() + "\n" : "") +
                    "Maximum you can still pack: " + maxCanPack + " " + prod.getUnit());
            return;
        }

        for (DeliveryItemDTO item : packingList) {
            if (item.getProductId() == prod.getProductId()) {
                item.setPackedQty(item.getPackedQty().add(qty));
                tblPackingItems.refresh();
                txtPackedQty.clear();
                return;
            }
        }

        packingList.add(new DeliveryItemDTO(0, 0, prod.getProductId(), prod.getName(), qty, BigDecimal.ZERO));
        txtPackedQty.clear();
    }

    @FXML
    public void handleRemoveItem(ActionEvent event) {
        DeliveryItemDTO selected = tblPackingItems.getSelectionModel().getSelectedItem();
        if (selected != null) {
            packingList.remove(selected);
        } else {
            AlertUtil.showWarning("Selection Required", "Please select an item from the list to remove.");
        }
    }

    @FXML
    public void handleSavePacked(ActionEvent event) {
        saveDelivery("PACKED");
    }

    @FXML
    public void handleDispatchNow(ActionEvent event) {
        if (cmbVehicle.getValue() == null) {
            AlertUtil.showWarning("Vehicle Required", "Please assign a delivery vehicle to dispatch.");
            return;
        }
        if (cmbDeliveryStaff.getValue() == null) {
            AlertUtil.showWarning("Driver Required", "Please assign a delivery driver to dispatch.");
            return;
        }
        saveDelivery("DISPATCHED");
    }

    private void saveDelivery(String status) {
        if (cmbShop.getValue() == null) {
            AlertUtil.showWarning("Shop Required", "Please select destination shop.");
            return;
        }
        if (dtDeliveryDate.getValue() == null) {
            AlertUtil.showWarning("Date Required", "Please choose delivery date.");
            return;
        }
        if (cmbPackedBy.getValue() == null) {
            AlertUtil.showWarning("Packing Staff Required", "Please select packing staff member.");
            return;
        }
        if (packingList.isEmpty()) {
            AlertUtil.showWarning("Items Required", "Please pack at least one product into this delivery.");
            return;
        }

        try {
            Long vehicleId = cmbVehicle.getValue() != null ? cmbVehicle.getValue().getVehicleId() : null;
            Long deliveryStaffId = cmbDeliveryStaff.getValue() != null ? cmbDeliveryStaff.getValue().getEmployeeId() : null;

            DeliveryDTO dto = new DeliveryDTO(
                    0,
                    cmbShop.getValue().getShopId(),
                    "",
                    vehicleId,
                    "",
                    deliveryStaffId,
                    "",
                    cmbPackedBy.getValue().getEmployeeId(),
                    "",
                    Date.valueOf(dtDeliveryDate.getValue()),
                    status
            );

            for (DeliveryItemDTO item : packingList) {
                dto.getItems().add(new DeliveryItemDTO(0, 0, item.getProductId(), item.getProductName(), item.getPackedQty(), BigDecimal.ZERO));
            }

            if (deliveryBO.prepareDelivery(dto)) {
                if ("DISPATCHED".equals(status)) {
                    deliveryBO.dispatchDelivery(dto.getDeliveryId(), vehicleId, deliveryStaffId);
                }
                AlertUtil.showInfo("Delivery Saved", "Delivery #" + dto.getDeliveryId() + " successfully created with status: " + status);
                handleClearForm(null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Database Error", e.getMessage());
        }
    }

    @FXML
    public void handleClearForm(ActionEvent event) {
        cmbShop.getSelectionModel().clearSelection();
        cmbVehicle.getSelectionModel().clearSelection();
        cmbDeliveryStaff.getSelectionModel().clearSelection();
        cmbProduct.getSelectionModel().clearSelection();
        txtPackedQty.clear();
        packingList.clear();
    }
}
