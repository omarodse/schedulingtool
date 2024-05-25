package main;

import controller.Customers;
import controller.MainScreen;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utilities.DBConnection;
import utilities.ManageState;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import static utilities.ManageState.*;

/**
 * Main class that launches the scheduling desktop application.
 * <p>
 * This class is responsible for initializing and displaying the primary stage and scene,
 * serving as the entry point for the JavaFX application.
 *<p>
 */
public class Main extends Application {
    /**
     * Starts the primary stage for this application.
     * <p>
     * This method loads the main application scene from an FXML file and sets it on the primary stage.
     *
     */

    private StackPane root = new StackPane();
    private static ResourceBundle resourceBundle;
    private static MainScreen mainScreenController;

    public static MainScreen getMainScreenController() {
        return mainScreenController;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        resourceBundle = getRB();

        ManageState.setPrimaryStage(primaryStage);
        initLocale();

        Node root = loadView("/view/LoginForm.fxml", resourceBundle);
        if (root == null) {
            System.out.println("Failed to load the login form.");
            return;
        }

        Scene scene = new Scene((Parent) root, 800, 600);
        primaryStage.setTitle(resourceBundle.getString("window.title"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * <p>
     * The Javadoc files are located in inventoryfxmlapp/javadoc
     * <p>
     * @param args the command line arguments
     * <p>
     */
    public static void main(String[] args) {
        DBConnection.openConnection();
        launch(args);
        DBConnection.closeConnection();
    }

}
