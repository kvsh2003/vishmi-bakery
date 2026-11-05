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
import lk.ijse.vishmibakehouse.dto.EmployeeDTO;
import lk.ijse.vishmibakehouse.dto.UserAccountDTO;
import lk.ijse.vishmibakehouse.util.AlertUtil;
import lk.ijse.vishmibakehouse.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

public class EmployeesController {

    @FXML
    private TextField txtEmpName;

    @FXML
    private TextField txtEmpPhone;

    @FXML
    private TextField txtEmpAddress;

    @FXML
    private ComboBox<String> cmbJobRole;

    @FXML
    private CheckBox chkEmpActive;

    @FXML
    private ComboBox<EmployeeDTO> cmbAccountEmployee;

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtAccountPassword;

    @FXML
    private ComboBox<String> cmbAccessRole;

    @FXML
    private CheckBox chkAccountActive;

    @FXML
    private TableView<EmployeeDTO> tblEmployees;

    @FXML
    private TableColumn<EmployeeDTO, Long> colEmpId;
    @FXML
    private TableColumn<EmployeeDTO, String> colEmpName;
    @FXML
    private TableColumn<EmployeeDTO, String> colEmpPhone;
    @FXML
    private TableColumn<EmployeeDTO, String> colEmpAddress;
    @FXML
    private TableColumn<EmployeeDTO, String> colEmpRole;
    @FXML
    private TableColumn<EmployeeDTO, Boolean> colEmpActive;

    private final EmployeeBO employeeBO = BOFactory.getInstance().getBO(BOTypes.EMPLOYEE);

    @FXML
    public void initialize() {
        cmbJobRole.setItems(FXCollections.observableArrayList("OWNER", "MANAGER", "PRODUCTION", "SALES", "PACKING", "DELIVERY"));
        cmbJobRole.getSelectionModel().select("PRODUCTION");

        cmbAccessRole.setItems(FXCollections.observableArrayList("OWNER", "MANAGER", "PRODUCTION", "SALES", "PACKING", "DELIVERY"));
        cmbAccessRole.getSelectionModel().select("PRODUCTION");

        colEmpId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        colEmpName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmpPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmpAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colEmpRole.setCellValueFactory(new PropertyValueFactory<>("jobRole"));
        colEmpActive.setCellValueFactory(new PropertyValueFactory<>("active"));

        loadEmployeeDropdowns();
        loadEmployeesTable();
    }

    private void loadEmployeeDropdowns() {
        try {
            List<EmployeeDTO> employees = employeeBO.getActiveEmployees();
            cmbAccountEmployee.setItems(FXCollections.observableArrayList(employees));
            cmbAccountEmployee.setConverter(new StringConverter<>() {
                @Override public String toString(EmployeeDTO object) { return object != null ? object.getName() + " (" + object.getJobRole() + ")" : ""; }
                @Override public EmployeeDTO fromString(String string) { return null; }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadEmployeesTable() {
        try {
            List<EmployeeDTO> list = employeeBO.getAllEmployees();
            tblEmployees.setItems(FXCollections.observableArrayList(list));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSaveEmployee(ActionEvent event) {
        String name = txtEmpName.getText() != null ? txtEmpName.getText().trim() : "";
        String phone = txtEmpPhone.getText() != null ? txtEmpPhone.getText().trim() : "";
        String addr = txtEmpAddress.getText() != null ? txtEmpAddress.getText().trim() : "";

        if (!ValidationUtil.isValidName(name)) {
            AlertUtil.showWarning("Name Required", "Please enter a valid employee name.");
            return;
        }
        if (!ValidationUtil.isValidPhone(phone)) {
            AlertUtil.showWarning("Phone Required", "Please enter a valid phone number.");
            return;
        }

        try {
            EmployeeDTO dto = new EmployeeDTO(0, name, phone, addr, cmbJobRole.getValue(), chkEmpActive.isSelected());
            if (employeeBO.saveEmployee(dto)) {
                AlertUtil.showInfo("Employee Registered", "Staff record for " + name + " saved.");
                handleClearEmployee(null);
                loadEmployeeDropdowns();
                loadEmployeesTable();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Database Error", e.getMessage());
        }
    }

    @FXML
    public void handleClearEmployee(ActionEvent event) {
        txtEmpName.clear();
        txtEmpPhone.clear();
        txtEmpAddress.clear();
        cmbJobRole.getSelectionModel().select("PRODUCTION");
        chkEmpActive.setSelected(true);
    }

    @FXML
    public void handleSaveAccount(ActionEvent event) {
        EmployeeDTO emp = cmbAccountEmployee.getValue();
        if (emp == null) {
            AlertUtil.showWarning("Employee Required", "Please select an employee.");
            return;
        }
        String username = txtUsername.getText() != null ? txtUsername.getText().trim() : "";
        String password = txtAccountPassword.getText() != null ? txtAccountPassword.getText().trim() : "";

        if (username.isEmpty()) {
            AlertUtil.showWarning("Username Required", "Please enter a username.");
            return;
        }
        if (password.length() < 6) {
            AlertUtil.showWarning("Password Too Short", "Password must be at least 6 characters.");
            return;
        }

        try {
            UserAccountDTO dto = new UserAccountDTO(0, emp.getEmployeeId(), username, cmbAccessRole.getValue(), chkAccountActive.isSelected());
            dto.setPassword(password);

            if (employeeBO.saveUserAccount(dto)) {
                AlertUtil.showInfo("Account Created", "User login credentials granted for " + emp.getName() + " (" + username + ").");
                txtUsername.clear();
                txtAccountPassword.clear();
                cmbAccountEmployee.getSelectionModel().clearSelection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Database Error", e.getMessage());
        }
    }
}
