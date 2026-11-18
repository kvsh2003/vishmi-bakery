package lk.ijse.vishmibakehouse.util;

import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;

public class DialogUtil {

    /**
     * Styles any JavaFX Dialog with the modern application Emerald design system
     * and centers it squarely over the primary application window.
     */
    public static void styleAndCenter(Dialog<?> dialog) {
        Stage owner = NavigationUtil.getPrimaryStage();
        if (owner != null) {
            dialog.initOwner(owner);
        }
        dialog.initModality(Modality.APPLICATION_MODAL);

        DialogPane pane = dialog.getDialogPane();
        URL css = DialogUtil.class.getResource("/css/style.css");
        if (css != null) {
            pane.getStylesheets().add(css.toExternalForm());
        }

        // Center on parent window when shown
        dialog.setOnShown(e -> {
            Stage stage = (Stage) pane.getScene().getWindow();
            if (owner != null && owner.isShowing()) {
                double x = owner.getX() + (owner.getWidth() - stage.getWidth()) / 2.0;
                double y = owner.getY() + (owner.getHeight() - stage.getHeight()) / 2.0;
                stage.setX(Math.max(0, x));
                stage.setY(Math.max(0, y));
            }
        });
    }
}

