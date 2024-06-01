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
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ResourceBundle;

import static DAO.AppointmentDAO.*;
import static DAO.ContactDAO.getAllContacts;
import static utilities.ManageState.getRB;
import static utilities.ManageState.loadView;

/**
 * Controller for the Overview GUI of the application.
 * This class is responsible for handling all user interactions on the Overview GUI,
 * including initializing all the dynamic information shown on this view.
 */
public class Overview implements Initializable, InitializableWithData {

    @FXML
    public BorderPane mainBorderPane;
    @FXML
    public Button customersButton;
    @FXML
    public Button overviewButton;
    @FXML
    public Button appointmentsButton;
    public Label nextAppointment;
    public ComboBox<Contact> contactComboBox;
    public TableView<Appointment> appointmentsPerContact;
    public TableColumn appointmentID;
    public TableColumn title;
    public TableColumn type;
    public TableColumn description;
    public TableColumn<Appointment, LocalDateTime> start;
    public TableColumn<Appointment, LocalDateTime> end;
    public TableColumn customerID;
    public Label appointmentsTomorrow;
    public Label addButton;
    private ResourceBundle rb = getRB();

    /**
     * Initializes the controller class by setting up the necessary UI components and loading initial data.
     * This includes populating combo boxes with contacts, displaying the count of tomorrow's appointments,
     * attempting to fetch and display an upcoming appointment for the user, and configuring table views for displaying
     * appointments.
     * The method handles data fetching operations, configures cell factories for table columns to format date and time
     * data appropriately, and sets up PropertyValueFactory for various table columns to bind them to the respective
     * properties of Appointment objects.
     * @param url The location used to resolve relative paths for the root object, or {@code null} if the location is not known.
     * @param resourceBundle The resources used to localize the root object, typically containing localized strings.
     * @throws RuntimeException if there is an SQLException during fetching the upcoming appointment, encapsulating
     * the original SQLException inside the RuntimeException.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Load all contacts into the contactComboBox
        contactComboBox.setItems(getAllContacts());

        // Set the text for appointmentsTomorrow to the count of tomorrow's appointments
        appointmentsTomorrow.setText(String.valueOf(getCountOfAppointmentsForNextDay()));

        Appointment upcomingAppointment;
        try {
            // Attempt to fetch the upcoming appointment for the user based on their system's default timezone
            upcomingAppointment = getUpcomingAppointmentForUser(ZoneId.systemDefault());
        } catch (SQLException e) {
            // Wrap and rethrow any SQL exceptions as unchecked exceptions
            throw new RuntimeException(e);
        }

        // Display the fetched upcoming appointment if available
        displayUpcomingAppointment(upcomingAppointment);

        // Set items in the appointmentsPerContact TableView
        appointmentsPerContact.setItems(getAllAppointments());

        // Configure table column factories and cell factories
        setupTableColumnFactories();
    }

    /**
     * Sets up PropertyValueFactory for each relevant column in the appointments tables. It also configures
     * custom cell factories for date and time columns to properly format LocalDateTime objects taking into account
     * the user's time zone.
     */
    private void setupTableColumnFactories() {
        appointmentID.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Configuring the start date column to format date and time according to system default timezone
        start.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        start.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    ZonedDateTime zdt = item.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault());
                    setText(zdt.toLocalDateTime().toString());
                }
            }
        });

        // Configuring the end date column similarly to start date column
        end.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        end.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    ZonedDateTime zdt = item.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault());
                    setText(zdt.toLocalDateTime().toString());
                }
            }
        });

        customerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
    }

    /**
     * Initializes the controller with dynamic data when required. This method allows for the flexible
     * initialization of the controller's state based on data passed at runtime.
     * @param data The data object used for initialization, which can vary depending on the specific use case.
     */
    @Override
    public void initializeData(Object data) {

    }

    /**
     * Displays details about an upcoming appointment on the main screen if an appointment is provided.
     * This method updates the `nextAppointment` label with the appointment's ID and start date, formatting the text
     * for easy reading. If no appointment is provided, it does nothing, leaving the previous content (if any) unchanged.
     * @param appointment The appointment to display. If null, no changes are made to the display.
     */
    public void displayUpcomingAppointment(Appointment appointment) {
        if (appointment != null) {
            // Retrieve the appointment ID and start date, converting them to string
            String appointmentID = String.valueOf(appointment.getAppointmentID());
            String startDate = String.valueOf(appointment.getStartDate());

            // Set the text of the nextAppointment label to show appointment details
            nextAppointment.setText("Appointment ID: " + appointmentID + "\n\nStart Date: " + startDate);
        }
    }

    /**
     * Handles the action triggered by clicking the 'Customers' button. This method changes the visual styling of the
     * navigation buttons to indicate the current active section and loads the 'Customers' view into the center of the
     * main border pane.
     * @param actionEvent The event that triggered this action.
     */
    public void onCustomers(ActionEvent actionEvent) {
        // Update button styles to indicate active and inactive sections
        overviewButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgba(255, 255, 255, 0.6);");
        appointmentsButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgba(255, 255, 255, 0.6);");
        customersButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgba(255, 255, 255, 1);");

        // Load the Customers view into the main pane
        mainBorderPane.setCenter(loadView("/view/Customers.fxml", rb));
    }

    /**
     * Handles the action triggered by clicking the 'Overview' button. Updates UI styling and loads the 'Overview' view.
     * @param actionEvent The event that triggered this action.
     */
    public void onOverview(ActionEvent actionEvent) {
        // Update button styles to reflect the current selection
        customersButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgba(255, 255, 255, 0.6);");
        overviewButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgba(255, 255, 255, 1);");
        appointmentsButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgba(255, 255, 255, 0.6);");

        // Load the Overview view into the main pane
        mainBorderPane.setCenter(loadView("/view/Overview.fxml", rb));
    }

    /**
     * Activates when the 'Appointments' button is clicked. It updates the UI to reflect the current section and loads
     * the 'Appointments' view into the main display area.
     * @param actionEvent The event that triggered this action.
     */
    public void onAppointments(ActionEvent actionEvent) {
        // Visual update to navigation buttons
        customersButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgba(255, 255, 255, 0.6);");
        overviewButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgba(255, 255, 255, 0.6);");
        appointmentsButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgba(255, 255, 255, 1);");

        // Load the Appointments view
        mainBorderPane.setCenter(loadView("/view/Appointments.fxml", rb));
    }


    /**
     * Handles the selection action on the contact combo box. This method updates the appointments table
     * to display appointments associated with the selected contact.
     * @param actionEvent The event that triggered this action, such as selecting an item from the combo box.
     */
    public void onContactCombo(ActionEvent actionEvent) {
        // Retrieve the selected contact from the combo box
        Contact contact = contactComboBox.getValue();

        // If a contact is selected, update the table with appointments for that contact
        if (contact != null) {
            appointmentsPerContact.setItems(getAppointmentsPerContact(contact));
        }
    }

    /**
     * Handles the click event on the 'Add' button. This method sets the center of the main border pane
     * to the form for adding a new appointment.
     * @param mouseEvent The mouse event that triggered this action.
     */
    public void onAddButton(MouseEvent mouseEvent) {
        // Load the Add Appointment form into the center of the main border pane
        mainBorderPane.setCenter(loadView("/view/AddAppointmentForm.fxml", rb));
    }

}
