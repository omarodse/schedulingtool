package utilities;

import controller.InitializableWithData;
import controller.MainScreen;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.Appointment;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static DAO.AppointmentDAO.getAllAppointments;
import static DAO.AppointmentDAO.getAllAppointmentsForCustomer;

public class ManageState {
    private static Stage primaryStage;
    private static BorderPane mainBorderPane;
    private static ResourceBundle resourceBundle;

    static {
        initLocale();
    }


    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static ResourceBundle getRB() {
        return resourceBundle;
    }

    public static void switchScene(String title, Scene scene) {
        if (primaryStage == null) {
            throw new IllegalStateException("Primary Stage is not initialized.");
        }
        primaryStage.setTitle(title);  // Set the title of the stage
        primaryStage.setScene(scene);  // Set the new scene
        primaryStage.show();  // Show the stage if not already visible or refresh it
    }

    public static Scene getScene(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(ManageState.class.getResource(fxml));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        MainScreen controller = loader.getController();
        scene.setUserData(controller);
        return scene;
    }

    public static void setMainBorderPane(BorderPane borderPane) {
        mainBorderPane = borderPane;
    }

    public static BorderPane getMainBorderPane() {
        return mainBorderPane;
    }

    public static Node loadView(String fxml, ResourceBundle resourceBundle) {
        try {
            FXMLLoader loader = new FXMLLoader(ManageState.class.getResource(fxml), resourceBundle);
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately - maybe log it and/or return an error message to the UI
            return null;
        }
    }

    public static Node loadView(String fxml, ResourceBundle resourceBundle, Object dataModel) {
        try {
            FXMLLoader loader = new FXMLLoader(ManageState.class.getResource(fxml), resourceBundle);
            Node view = loader.load();
            Object controller = loader.getController();
            if (controller instanceof InitializableWithData) {
                ((InitializableWithData) controller).initializeData(dataModel);
            }
            return view;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(ManageState.class.getResource(fxmlPath));
            Node view = loader.load();
            mainBorderPane.setCenter(view);
        } catch (IOException e) {
            System.err.println("Error loading view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void initLocale() {
        Locale locale = Locale.getDefault();
        resourceBundle = ResourceBundle.getBundle("resources.UILanguage", locale);
    }

    public static boolean validateAppointment(ZonedDateTime startTime, ZonedDateTime endTime, ZoneId userTimeZone, int customerID) {

        // Check if the end time is before the start time
        if (endTime.isBefore(startTime)) {
            showAlert("Invalid Date/Time", "End time cannot be before start time.");
            return false;
        }

        // Check for minimum duration of 15 minutes
        if (Duration.between(startTime, endTime).toMinutes() < 15) {
            showAlert("Duration Error", "Appointments must be at least 15 minutes long.");
            return false;
        }

        // Check business hours in ET
        if (!isWithinBusinessHours(startTime, endTime)) {
            showAlert("Business Hours Violation", "Appointment must be between 08:00 and 10:00 ET.");
            return false;
        }

        // Check for overlapping appointments
        if (hasOverlappingAppointments(startTime.toLocalDateTime(), endTime.toLocalDateTime(), customerID)) {
            showAlert("Overlap Error", "This appointment overlaps with an existing one.");
            return false;
        }

        // If all checks pass, proceed to save the appointment
        return true;
    }
    public static boolean validateAppointment(ZonedDateTime startTime, ZonedDateTime endTime, ZoneId userTimeZone, int customerID, int appointmentID) {

        ZonedDateTime now = ZonedDateTime.now(userTimeZone);
        if (startTime.isBefore(now)) {
            showAlert("Invalid Date/Time", "Cannot schedule appointments in the past.");
            return false;
        }

        // Check if the end time is before the start time
        if (endTime.isBefore(startTime)) {
            showAlert("Invalid Date/Time", "End time cannot be before start time.");
            return false;
        }

        // Check for minimum duration of 15 minutes
        if (Duration.between(startTime, endTime).toMinutes() < 15) {
            showAlert("Duration Error", "Appointments must be at least 15 minutes long.");
            return false;
        }

        // Check business hours in ET
        if (!isWithinBusinessHours(startTime, endTime)) {
            showAlert("Business Hours Violation", "Appointment must be between 08:00 and 10:00 ET.");
            return false;
        }

        // Check for overlapping appointments
        if (hasOverlappingAppointments(startTime.toLocalDateTime(), endTime.toLocalDateTime(), customerID, appointmentID)) {
            showAlert("Overlap Error", "This appointment overlaps with an existing one.");
            return false;
        }

        // If all checks pass, proceed to save the appointment
        return true;
    }

    private static boolean isWithinBusinessHours(ZonedDateTime startTime, ZonedDateTime endTime) {

        ZonedDateTime startET = startTime.withZoneSameInstant(ZoneId.of("America/New_York"));
        ZonedDateTime endET = endTime.withZoneSameInstant(ZoneId.of("America/New_York"));

        LocalTime businessStart = LocalTime.of(8, 0);
        LocalTime businessEnd = LocalTime.of(22, 0);

        return !startET.toLocalTime().isBefore(businessStart) && !endET.toLocalTime().isAfter(businessEnd);
    }

    private static boolean hasOverlappingAppointments(LocalDateTime startUTC, LocalDateTime endUTC, int customerID) {
        List<Appointment> existingAppointments = getAllAppointmentsForCustomer(customerID);
        for (Appointment existing : existingAppointments) {
            if (existing.getCustomerID() == customerID &&
                    startUTC.isBefore(existing.getEndDate()) &&
                    endUTC.isAfter(existing.getStartDate())) {
                return true;  // Overlapping detected
            }
        }
        return false;  // No overlap
    }
    private static boolean hasOverlappingAppointments(LocalDateTime startUTC, LocalDateTime endUTC, int customerID, int editingAppointmentId) {
        List<Appointment> existingAppointments = getAllAppointmentsForCustomer(customerID);
        for (Appointment existing : existingAppointments) {
            // Check if the existing appointment is not the one being edited
            if (existing.getAppointmentID() != editingAppointmentId &&
                    startUTC.isBefore(existing.getEndDate()) &&
                    endUTC.isAfter(existing.getStartDate())) {
                return true;  // Overlapping detected
            }
        }
        return false;  // No overlap
    }

    public static String formatToLocalDateTimeString(LocalDateTime utcDateTime) {
        ZonedDateTime zonedUtcDateTime = utcDateTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime localDateTime = zonedUtcDateTime.withZoneSameInstant(ZoneId.systemDefault());
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    public static LocalDateTime convertToLocalDateTimeFromUserInput(LocalDate date, LocalTime time) {
        ZonedDateTime userZonedDateTime = LocalDateTime.of(date, time).atZone(ZoneId.systemDefault());
        ZonedDateTime utcDateTime = userZonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));
        return utcDateTime.toLocalDateTime();
    }

    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
