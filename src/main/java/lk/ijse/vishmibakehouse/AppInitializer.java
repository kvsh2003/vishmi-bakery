package lk.ijse.vishmibakehouse;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lk.ijse.vishmibakehouse.util.NavigationUtil;

import java.io.InputStream;

public class AppInitializer extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        NavigationUtil.init(primaryStage);

        Parent root = FXMLLoader.load(getClass().getResource("/view/01-login.fxml"));
        Scene scene = new Scene(root);

        primaryStage.setTitle("Vishmi Bakehouse Management System");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();

        InputStream iconStream = getClass().getResourceAsStream("/images/01-login.png");
        if (iconStream != null) {
            primaryStage.getIcons().add(new Image(iconStream));
        }

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
