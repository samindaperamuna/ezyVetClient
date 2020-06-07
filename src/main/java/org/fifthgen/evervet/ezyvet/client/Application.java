package org.fifthgen.evervet.ezyvet.client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.fifthgen.evervet.ezyvet.client.ui.MainController;

public class Application extends javafx.application.Application {

    private final static String STAGE_TITLE = "Evervet - ezyVet Client";
    private final static int WIDTH = 800;
    private final static int HEIGHT = 600;

    public static void main(String... args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ui/main.fxml"));

        // Load and getOrders the main controller.
        Parent root = loader.load();
        MainController controller = loader.getController();

        // Set stage.
        controller.stage = primaryStage;

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle(STAGE_TITLE);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("ui/images/logo.png")));
        primaryStage.setMinWidth(WIDTH);
        primaryStage.setWidth(WIDTH);
        primaryStage.setMinHeight(HEIGHT);
        primaryStage.setHeight(HEIGHT);

        // Exit the application on main window close.
        primaryStage.setOnCloseRequest(event -> System.exit(0));

        primaryStage.show();
    }
}
