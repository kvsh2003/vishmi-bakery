package lk.ijse.vishmibakehouse.util;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Optional;

public class AlertUtil {

    private static Pane notificationContainer;

    public static void setNotificationContainer(Pane container) {
        notificationContainer = container;
    }

    public enum ToastType {
        SUCCESS, ERROR, WARNING, INFO
    }

    public static void showSuccess(String title, String message) {
        showToast(ToastType.SUCCESS, title, message);
    }

    public static void showInfo(String title, String message) {
        if (title != null && (
                title.equalsIgnoreCase("Success") ||
                title.equalsIgnoreCase("Updated") ||
                title.equalsIgnoreCase("Saved") ||
                title.toLowerCase().contains("registered") ||
                title.toLowerCase().contains("created") ||
                title.toLowerCase().contains("completed") ||
                title.toLowerCase().contains("cleared")
        )) {
            showToast(ToastType.SUCCESS, title, message);
        } else {
            showToast(ToastType.INFO, title, message);
        }
    }

    public static void showError(String title, String message) {
        showToast(ToastType.ERROR, title, message);
    }

    public static void showWarning(String title, String message) {
        showToast(ToastType.WARNING, title, message);
    }

    private static void showToast(ToastType type, String title, String message) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> showToast(type, title, message));
            return;
        }

        if (notificationContainer != null) {
            displayToastInContainer(type, title, message, notificationContainer);
            return;
        }

        // Fallback: check if primaryStage scene root is available
        Stage owner = NavigationUtil.getPrimaryStage();
        if (owner != null && owner.getScene() != null && owner.getScene().getRoot() instanceof Pane rootPane) {
            displayToastInContainer(type, title, message, rootPane);
            return;
        }

        // Last resort: standard dialog with owner attachment (prevents separate detached window)
        Alert.AlertType alertType = switch (type) {
            case ERROR -> Alert.AlertType.ERROR;
            case WARNING -> Alert.AlertType.WARNING;
            case SUCCESS, INFO -> Alert.AlertType.INFORMATION;
        };
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        DialogUtil.styleAndCenter(alert);
        alert.showAndWait();
    }

    private static void displayToastInContainer(ToastType type, String title, String message, Pane container) {
        HBox toast = new HBox(12);
        toast.setAlignment(Pos.CENTER_LEFT);
        toast.getStyleClass().add("toast-card");

        String iconText;
        String typeClass;
        switch (type) {
            case SUCCESS -> {
                iconText = "✓";
                typeClass = "toast-success";
            }
            case ERROR -> {
                iconText = "✕";
                typeClass = "toast-error";
            }
            case WARNING -> {
                iconText = "⚠";
                typeClass = "toast-warning";
            }
            default -> {
                iconText = "ℹ";
                typeClass = "toast-info";
            }
        }
        toast.getStyleClass().add(typeClass);

        Label lblIcon = new Label(iconText);
        lblIcon.getStyleClass().add("toast-icon");

        VBox contentBox = new VBox(2);
        HBox.setHgrow(contentBox, Priority.ALWAYS);

        Label lblTitle = new Label(title != null ? title : "Notification");
        lblTitle.getStyleClass().add("toast-title");

        Label lblMsg = new Label(message != null ? message : "");
        lblMsg.getStyleClass().add("toast-message");
        lblMsg.setWrapText(true);

        contentBox.getChildren().addAll(lblTitle, lblMsg);

        Button btnClose = new Button("✕");
        btnClose.getStyleClass().add("toast-close-btn");

        toast.getChildren().addAll(lblIcon, contentBox, btnClose);

        container.getChildren().add(0, toast);

        // Entrance animation
        toast.setOpacity(0.0);
        toast.setTranslateY(-12.0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(220), toast);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(220), toast);
        slideIn.setFromY(-12.0);
        slideIn.setToY(0.0);

        fadeIn.play();
        slideIn.play();

        // Auto dismiss after 3.5 seconds
        PauseTransition delay = new PauseTransition(Duration.seconds(3.5));
        delay.setOnFinished(e -> dismissToast(toast, container));
        delay.play();

        btnClose.setOnAction(e -> {
            delay.stop();
            dismissToast(toast, container);
        });

        toast.setOnMouseClicked(e -> {
            delay.stop();
            dismissToast(toast, container);
        });
    }

    private static void dismissToast(HBox toast, Pane container) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(250), toast);
        fadeOut.setFromValue(toast.getOpacity());
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> container.getChildren().remove(toast));
        fadeOut.play();
    }

    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        DialogUtil.styleAndCenter(alert);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
