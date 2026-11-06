package lk.ijse.vishmibakehouse.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lk.ijse.vishmibakehouse.dto.UserAccountDTO;
import lk.ijse.vishmibakehouse.util.AlertUtil;
import lk.ijse.vishmibakehouse.util.NavigationUtil;
import lk.ijse.vishmibakehouse.util.SecurityContext;

import java.io.IOException;

public class MainLayoutController {

    @FXML
    private Label lblPageHeader;

    @FXML
    private Label lblCurrentUser;

    @FXML
    private Label lblUserRole;

    @FXML
    private StackPane centerContentPane;

    @FXML
    private VBox notificationBox;

    @FXML
    private Button btnNavDashboard;
    @FXML
    private Button btnNavProducts;
    @FXML
    private Button btnNavProduction;
    @FXML
    private Button btnNavIngredients;
    @FXML
    private Button btnNavPurchases;
    @FXML
    private Button btnNavUsage;
    @FXML
    private Button btnNavOutletSales;
    @FXML
    private Button btnNavPacking;
    @FXML
    private Button btnNavDeliveries;
    @FXML
    private Button btnNavSpecialOrders;
    @FXML
    private Button btnNavLeftovers;
    @FXML
    private Button btnNavEmployees;
    @FXML
    private Button btnNavReports;

    @FXML
    public void initialize() {
        NavigationUtil.setContentPane(centerContentPane);
        AlertUtil.setNotificationContainer(notificationBox);

        UserAccountDTO user = SecurityContext.getCurrentUser();
        if (user != null) {
            lblCurrentUser.setText(user.getEmployeeName() != null ? user.getEmployeeName() : user.getUsername());
            lblUserRole.setText(user.getAccessRole());

            applyPermissions(user.getAccessRole());
        }

        // Default initial view: Dashboard
        handleNavDashboard(null);
    }

    private void applyPermissions(String role) {
        if ("OWNER".equalsIgnoreCase(role) || "MANAGER".equalsIgnoreCase(role)) {
            // Owner and Manager have full system access
            return;
        }

        // Role-specific button permissions
        btnNavProducts.setDisable(!SecurityContext.canAccess("products"));
        btnNavIngredients.setDisable(!SecurityContext.canAccess("ingredients"));
        btnNavPurchases.setDisable(!SecurityContext.canAccess("purchases"));
        btnNavProduction.setDisable(!SecurityContext.canAccess("production"));
        btnNavUsage.setDisable(!SecurityContext.canAccess("usage"));
        btnNavOutletSales.setDisable(!SecurityContext.canAccess("outlet-sales"));
        btnNavPacking.setDisable(!SecurityContext.canAccess("packing-dispatch"));
        btnNavDeliveries.setDisable(!SecurityContext.canAccess("deliveries"));
        btnNavSpecialOrders.setDisable(!SecurityContext.canAccess("special-orders"));
        btnNavLeftovers.setDisable(!SecurityContext.canAccess("leftovers"));
        btnNavEmployees.setDisable(!SecurityContext.canAccess("employees"));
        btnNavReports.setDisable(!SecurityContext.canAccess("reports"));
    }

    private void setActiveButton(Button activeButton) {
        Button[] buttons = {
            btnNavDashboard, btnNavProducts, btnNavProduction, btnNavIngredients,
            btnNavPurchases, btnNavUsage, btnNavOutletSales, btnNavPacking,
            btnNavDeliveries, btnNavSpecialOrders, btnNavLeftovers, btnNavEmployees,
            btnNavReports
        };
        for (Button b : buttons) {
            if (b != null) {
                b.getStyleClass().remove("active");
            }
        }
        if (activeButton != null && !activeButton.getStyleClass().contains("active")) {
            activeButton.getStyleClass().add("active");
        }
    }

    private void loadView(String fxmlPath, String headerTitle, Button activeButton) {
        try {
            setActiveButton(activeButton);
            lblPageHeader.setText(headerTitle);
            NavigationUtil.navigateTo(fxmlPath);
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("View Navigation Error", "Could not load screen: " + fxmlPath + "\n" + e.getMessage());
        }
    }

    @FXML
    public void handleNavDashboard(ActionEvent event) {
        loadView("/view/02-dashboard.fxml", "Daily Operations Overview", btnNavDashboard);
    }

    @FXML
    public void handleNavProducts(ActionEvent event) {
        loadView("/view/03-products.fxml", "Bakery Products Catalog", btnNavProducts);
    }

    @FXML
    public void handleNavProduction(ActionEvent event) {
        loadView("/view/04-production.fxml", "Daily Production Management", btnNavProduction);
    }

    @FXML
    public void handleNavIngredients(ActionEvent event) {
        loadView("/view/05-ingredients.fxml", "Ingredient Inventory & Reorder Levels", btnNavIngredients);
    }

    @FXML
    public void handleNavPurchases(ActionEvent event) {
        loadView("/view/06-purchases.fxml", "Ingredient Purchases & Goods Received", btnNavPurchases);
    }

    @FXML
    public void handleNavUsage(ActionEvent event) {
        loadView("/view/07-usage.fxml", "Ingredient Consumption per Batch", btnNavUsage);
    }

    @FXML
    public void handleNavOutletSales(ActionEvent event) {
        loadView("/view/08-outlet-sales.fxml", "Bakery Outlet Point of Sale (POS)", btnNavOutletSales);
    }

    @FXML
    public void handleNavPacking(ActionEvent event) {
        loadView("/view/09-packing-dispatch.fxml", "Shop Packing & Delivery Dispatch", btnNavPacking);
    }

    @FXML
    public void handleNavDeliveries(ActionEvent event) {
        loadView("/view/10-deliveries.fxml", "Delivery Route & Fleet Management", btnNavDeliveries);
    }

    @FXML
    public void handleNavSpecialOrders(ActionEvent event) {
        loadView("/view/11-special-orders.fxml", "Wedding Cakes & Custom Special Orders", btnNavSpecialOrders);
    }

    @FXML
    public void handleNavLeftovers(ActionEvent event) {
        loadView("/view/12-leftovers.fxml", "Leftover Bread & Bread Crumb Production", btnNavLeftovers);
    }

    @FXML
    public void handleNavEmployees(ActionEvent event) {
        loadView("/view/13-employees.fxml", "Staff Personnel & User Access Accounts", btnNavEmployees);
    }

    @FXML
    public void handleNavReports(ActionEvent event) {
        loadView("/view/14-reports.fxml", "Business Intelligence Reports & Analytics", btnNavReports);
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        if (AlertUtil.showConfirmation("Confirm Logout", "Are you sure you want to log out of the system?")) {
            SecurityContext.clear();
            try {
                NavigationUtil.switchScene("/view/01-login.fxml", "Sign In - Vishmi Bakehouse");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
