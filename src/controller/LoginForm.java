package controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.Appointment;
import utilities.ManageState;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.ResourceBundle;

import static DAO.AppointmentDAO.getUpcomingAppointmentForUser;
import static DAO.UserDAO.userAccess;
import static utilities.ManageState.getRB;

/**
 * Controller for the Login Form UI.
 * This class handles the user interactions with the Login Form, allowing users
 * to sign in to the scheduling too application.
 */
public class LoginForm implements Initializable {
    public Label signInText;
    public Label locationLabel;
    public TextField emailField;
    public PasswordField passwordField;
    public Button loginButton;
    public Label loginError;
    private ResourceBundle rb = getRB();

    @Override
    /**
     * Initializes the login form controller. This method sets up the UI components with default values
     * and localizations from the resource bundle. It also sets the system's default time zone as text for
     * the location label to indicate the current time zone to the user.
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param rb The resource bundle used to localize the root object. It provides localized strings for UI components like labels and buttons.
     */
    public void initialize(URL url, ResourceBundle rb) {
        ZoneId zoneId = ZoneId.systemDefault();
        System.out.println(zoneId);
        locationLabel.setText(String.valueOf(zoneId));

        signInText.setText(rb.getString("sign_in"));
        emailField.setPromptText(rb.getString("email_prompt"));
        passwordField.setPromptText(rb.getString("password_prompt"));
        loginButton.setText(rb.getString("log_in"));
    }

    /**
     * Handles the login button action. This method validates the user credentials and processes the login attempt.
     * If successful, it logs the attempt, hides the error message, and loads the main application screen,
     * potentially passing any relevant data like upcoming appointments. If unsuccessful, it displays an error message.
     * @param actionEvent The event that triggered this method, generally the clicking of the login button.
     * @throws SQLException If there's an error during the database access required for user validation.
     * @throws IOException If there's an error loading the next scene or related resources.
     */
    public void onLoginButton(ActionEvent actionEvent) throws SQLException, IOException {
        String userName = emailField.getText();
        String password = passwordField.getText();

        try {
            boolean isSuccess = userAccess(userName, password);
            LoginTracker.logLoginAttempt(userName, isSuccess);
            if (isSuccess) {
                loginError.setVisible(false);  // Hide error message on successful login

                // Fetch upcoming appointment
                Appointment upcomingAppointment = getUpcomingAppointmentForUser(ZoneId.systemDefault());

                // Load main screen
                Scene scene = ManageState.getScene("/view/MainScreen.fxml");
                if (scene == null) {
                    throw new IllegalStateException("The scene is null.");
                }

                MainScreen controller = (MainScreen) scene.getUserData();
                if (controller != null && upcomingAppointment != null) {
                    controller.displayUpcomingAppointment(upcomingAppointment);
                } else {
                    System.out.println("Controller is null");
                }

                ManageState.switchScene("Calendar", scene);
            } else {
                loginError.setText(rb.getString("login_error"));
                loginError.setVisible(true);
            }
        } catch (SQLException | IOException e) {
            loginError.setText("Error");
            loginError.setVisible(true);
            e.printStackTrace();
        }
    }

}
