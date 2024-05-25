package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import model.Appointment;
import model.Contact;
import model.Customer;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ResourceBundle;

import static DAO.AppointmentDAO.*;
import static DAO.ContactDAO.getAllContacts;
import static utilities.ManageState.*;

/**
 * Controller class for the main screen of the application, handling navigation and interactions
 * within the primary user interface. This class initializes and handles user interactions with
 * various components such as buttons, tables, and combo boxes to manage customers, appointments,
 * and other functionalities.
 *
 * Implements both Initializable for basic initialization upon loading and InitializableWithData
 * for dynamic data-driven initialization.
 */
public class MainScreen implements Initializable, InitializableWithData {
    @FXML
    public BorderPane mainBorderPane;
    @FXML
    public Button customersButton;
    @FXML
    public Button overviewButton;
    @FXML
    public Button appointmentsButton;

    public Label nextAppointment;
    public Label addButton;
    public ComboBox<Contact> contactComboBox;
    public TableColumn appointmentID;
    public TableColumn title;
    public TableColumn type;
    public TableColumn description;
    public TableColumn<Appointment, LocalDateTime> start;
    public TableColumn<Appointment, LocalDateTime> end;
    public TableColumn customerID;
    public TableView<Appointment> appointmentsPerContact;
    public Label appointmentsTomorrow;

    private Contact contact;

    private Customer customer;
    private ResourceBundle rb = getRB();

    /**
     * Initializes the controller class. This method is automatically called after the FXML file has been loaded.
     * It sets up the components with necessary data and behaviors such as filling combo boxes and setting up table columns.
     *
     * @param url The location used to resolve relative paths for the root object, or {@code null} if unknown.
     * @param resourceBundle The resources used to localize the root object, or {@code null} if not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        contactComboBox.setItems(getAllContacts());
        appointmentsTomorrow.setText(String.valueOf(getCountOfAppointmentsForNextDay()));
        appointmentsPerContact.setItems(getAllAppointments());

        appointmentID.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));

        start.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        start.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    // Convert UTC LocalDateTime to local time zone
                    ZonedDateTime zdt = item.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault());
                    setText(zdt.toLocalDateTime().toString()); // Default LocalDateTime toString format
                }
            }
        });

        end.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        end.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    ZonedDateTime zdt = item.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault());
                    setText(zdt.toLocalDateTime().toString()); // Default LocalDateTime toString format
                }
            }
        });

        customerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
    }

    /**
     * Initializes the controller with dynamic data when required. This method allows for the flexible
     * initialization of the controller's state based on data passed at runtime.
     *
     * @param data The data object used for initialization, which can vary depending on the specific use case.
     */
    @Override
    public void initializeData(Object data) {

    }

    public void displayUpcomingAppointment(Appointment appointment) {
        if (appointment != null) {
            String appointmentID = String.valueOf(appointment.getAppointmentID());
            String startDate = String.valueOf(appointment.getStartDate());
            nextAppointment.setText("Appointment ID: " + appointmentID + "\n\n" + startDate);
        }
    }

    /**
     * Handles the action triggered by clicking the 'Customers' button. This method changes the
     * center pane of the main border pane to the customers view, allowing the user to manage customer records.
     *
     * @param actionEvent The event that triggered this action.
     */
    public void onCustomers(ActionEvent actionEvent) {
        overviewButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgba(255, 255, 255, 0.6); -fx-cursor: hand");
        appointmentsButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgba(255, 255, 255, 0.6); -fx-cursor: hand");
        customersButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgba(255, 255, 255, 1);");
        mainBorderPane.setCenter(loadView("/view/Customers.fxml", rb));
    }

    /**
     * Handles the action triggered by clicking the 'Overview' button. This method changes the
     * center pane of the main border pane to the overview view.
     *
     * @param actionEvent The event that triggered this action.
     */
    public void onOverview(ActionEvent actionEvent) {
        customersButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgba(255, 255, 255, 0.6); -fx-cursor: hand");
        overviewButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgba(255, 255, 255, 1); -fx-cursor: hand");
        appointmentsButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgba(255, 255, 255, 0.6); -fx-cursor: hand");
        mainBorderPane.setCenter(loadView("/view/Overview.fxml", rb));
    }

    /**
     * Handles the action triggered by clicking the 'Appointment' button. This method changes the
     * center pane of the main border pane to the appointments view, allowing the user to manage appointment records.
     *
     * @param actionEvent The event that triggered this action.
     */
    public void onAppointments(ActionEvent actionEvent) {
        customersButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgba(255, 255, 255, 0.6); -fx-cursor: hand");
        overviewButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgba(255, 255, 255, 0.6); -fx-cursor: hand");
        appointmentsButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgba(255, 255, 255, 1);");
        mainBorderPane.setCenter(loadView("/view/Appointments.fxml", rb));
    }

    /**
     * Handles the action triggered by selecting an item in the contact combo box. This method updates the appointments table
     * to show appointments related to the selected contact. It ensures that only the appointments linked to the selected
     * contact are displayed in the appointments table.
     *
     * @param actionEvent The event that triggered this action, typically from a user interaction with the contactComboBox.
     */
    public void onContactCombo(ActionEvent actionEvent) {
        Contact contact = contactComboBox.getValue();
        if (contact != null) {
            appointmentsPerContact.setItems(getAppointmentsPerContact(contact));
        }
    }

    /**
     * Handles mouse click events on the 'Add' button to initiate the process of adding a new appointment.
     * This method sets the appearance of other navigation buttons to a less highlighted state and loads
     * the form to add a new appointment into the central panel of the main application window. It ensures
     * the user interface is responsive and intuitive during the transition between views.
     *
     * @param mouseEvent The mouse event that triggered this method, typically the clicking of the 'Add' button.
     */
    public void onAddButton(MouseEvent mouseEvent) {
        customersButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgba(255, 255, 255, 0.6); -fx-cursor: hand");
        overviewButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgba(255, 255, 255, 0.6); -fx-cursor: hand");
        appointmentsButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgba(255, 255, 255, 1);");
        // Load the appointment addition form into the main application window.
        mainBorderPane.setCenter(loadView("/view/AddAppointmentForm.fxml", rb));
    }

}
