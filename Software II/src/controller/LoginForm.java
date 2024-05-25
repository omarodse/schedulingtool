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

/**
 * Controller for the Login Form UI.
 * <p>
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

    /**
     * Initializes the controller class.
     * <p>
     * This method is automatically called after the FXML file has been loaded. It can be used
     * to perform initializations such as setting event handlers or configuring UI controls.
     *
     * @param url The location used to resolve relative paths for the root object, or {@code null} if unknown.
     * @param rb The resources used to localize the root object, or {@code null} if not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ZoneId zoneId = ZoneId.systemDefault();
        locationLabel.setText(String.valueOf(zoneId));

        signInText.setText(rb.getString("sign_in"));
        emailField.setPromptText(rb.getString("email_prompt"));
        passwordField.setPromptText(rb.getString("password_prompt"));
        loginButton.setText(rb.getString("log_in"));
        loginError.setText(rb.getString("login_error"));
    }

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

                // Pass appointment data to the main scene if necessary
                MainScreen controller = (MainScreen) scene.getUserData();
                if (controller != null && upcomingAppointment != null) {
                    controller.displayUpcomingAppointment(upcomingAppointment);
                } else {
                    System.out.println("Controller is null");
                }

                ManageState.switchScene("Calendar", scene);
            } else {
                loginError.setText("Invalid username or password. Please try again.");
                loginError.setVisible(true);
            }
        } catch (SQLException e) {
            loginError.setText("Login failed due to database error. Please contact support.");
            loginError.setVisible(true);
            e.printStackTrace();  // Log the exception to console or a log file
        } catch (IOException e) {
            loginError.setText("Unable to load the next scene. Please restart the application.");
            loginError.setVisible(true);
            e.printStackTrace();  // Log the exception to console or a log file
        }
    }
}
