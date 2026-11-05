package lk.ijse.vishmibakehouse.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class NavigationUtil {
    private static Stage primaryStage;
    private static Pane contentPane;

    public static void init(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setContentPane(Pane pane) {
        contentPane = pane;
    }

    public static void navigateTo(String fxmlPath) throws IOException {
        if (contentPane != null) {
            URL resource = NavigationUtil.class.getResource(fxmlPath);
            if (resource == null) {
                throw new IOException("FXML file not found: " + fxmlPath);
            }
            Parent node = FXMLLoader.load(resource);
            contentPane.getChildren().setAll(node);
        }
    }

    public static void switchScene(String fxmlPath, String title) throws IOException {
        if (primaryStage != null) {
            URL resource = NavigationUtil.class.getResource(fxmlPath);
            if (resource == null) {
                throw new IOException("FXML file not found: " + fxmlPath);
            }
            Parent root = FXMLLoader.load(resource);
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            primaryStage.centerOnScreen();
            primaryStage.show();
        }
    }
}
