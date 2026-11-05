package lk.ijse.vishmibakehouse.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lk.ijse.vishmibakehouse.bo.BOFactory;
import lk.ijse.vishmibakehouse.bo.BOTypes;
import lk.ijse.vishmibakehouse.bo.custom.AuthBO;
import lk.ijse.vishmibakehouse.dto.UserAccountDTO;
import lk.ijse.vishmibakehouse.util.AlertUtil;
import lk.ijse.vishmibakehouse.util.NavigationUtil;
import lk.ijse.vishmibakehouse.util.SecurityContext;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblError;

    @FXML
    private Button btnLogin;

    private final AuthBO authBO = BOFactory.getInstance().getBO(BOTypes.AUTH);

    @FXML
    public void initialize() {
        lblError.setVisible(false);
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = txtUsername.getText() != null ? txtUsername.getText().trim() : "";
        String password = txtPassword.getText() != null ? txtPassword.getText().trim() : "";

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        try {
            Optional<UserAccountDTO> optUser = authBO.authenticate(username, password);
            if (optUser.isPresent()) {
                UserAccountDTO user = optUser.get();
                SecurityContext.setCurrentUser(user);
                NavigationUtil.switchScene("/view/main_layout.fxml", "Vishmi Bakehouse Management System");
            } else {
                showError("Invalid username or password. Please try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Database Connection Error", "Unable to connect to database:\n" + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Navigation Error", "Could not load main application layout:\n" + e.getMessage());
        }
    }

    private void showError(String msg) {
        lblError.setText(msg);
        lblError.setVisible(true);
    }
}
