package controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import model.Appointment;

import java.net.URL;
import java.time.*;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static DAO.AppointmentDAO.editAppointment;
import static DAO.ContactDAO.getAllContacts;
import static DAO.CustomerDAO.getAllCustomerIDs;
import static DAO.UserDAO.getAllUserIDs;
import static utilities.ManageState.*;

/**
 * Controller class for the Modify Appointment screen of the application. This class handles
 * the user interface where users can edit details of existing appointments. It provides text fields,
 * date pickers, and combo boxes for user input and allows saving or canceling changes.
 *
 * Implements both Initializable for basic initialization upon loading and InitializableWithData
 * for dynamic data-driven initialization, allowing the form to be pre-populated with existing appointment details.
 */
public class ModifyAppointment implements Initializable, InitializableWithData {

    public BorderPane mainBorderPane;
    public TextField titleField;
    public TextField descriptionField;
    public TextField typeField;
    public TextField idField;
    public DatePicker startDatePickerField;
    public ComboBox<String> startTimeHour;
    public ComboBox<String> startTimeMinutes;
    public TextField locationField;
    public DatePicker endDatePicker;
    public ComboBox<String> endTimeHour;
    public ComboBox<String> endTimeMinutes;
    public ComboBox customerIDComboField;
    public ComboBox contactComboField;
    public ComboBox userIDField;
    public Button cancelButton;
    public Button saveButton;
    private Appointment appointment;
    private ResourceBundle rb = getRB();


    /**
     * Initializes the controller by setting up UI components specific to appointment management.
     * This method configures date pickers and populates combo boxes with appropriate time options and entity identifiers
     * such as contacts, user IDs, and customer IDs to facilitate the scheduling or editing of appointments.
     *
     * The method leverages Java streams to generate time options in a 24-hour format for both hours and minutes,
     * ensuring that time selection is intuitive and efficient.
     *
     * @param url The location used to resolve relative paths for the root object, or {@code null} if the location is not known.
     * @param resourceBundle The resources used to localize the root object, providing localized strings for UI components.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Setup date pickers to use specific date formats or constraints
        configureDatePickers();

        // Populate time selection combo boxes with hours and minutes, formatted to two digits
        startTimeHour.getItems().addAll(
                IntStream.rangeClosed(0, 23)
                        .mapToObj(i -> String.format("%02d", i))
                        .collect(Collectors.toList())
        );
        startTimeMinutes.getItems().addAll(
                IntStream.rangeClosed(0, 59)
                        .mapToObj(i -> String.format("%02d", i))
                        .collect(Collectors.toList())
        );

        endTimeHour.getItems().addAll(
                IntStream.rangeClosed(0, 23)
                        .mapToObj(i -> String.format("%02d", i))
                        .collect(Collectors.toList())
        );
        endTimeMinutes.getItems().addAll(
                IntStream.rangeClosed(0, 59)
                        .mapToObj(i -> String.format("%02d", i))
                        .collect(Collectors.toList())
        );

        // Load dynamic data into combo boxes for contacts, user IDs, and customer IDs
        contactComboField.setItems(getAllContacts());
        userIDField.setItems(getAllUserIDs());
        customerIDComboField.setItems(getAllCustomerIDs());
    }

    /**
     * Initializes the controller with specific appointment data for editing. This method is called to populate the form
     * fields with existing appointment details when an appointment is selected for modification. It handles the conversion
     * of UTC dates and times to the system's default time zone to ensure the user interface reflects the local time.
     *
     * @param data The appointment data passed to the controller, expected to be an instance of Appointment.
     *             This data is used to pre-populate the form fields with existing values, allowing for easier editing.
     */
    public void initializeData(Object data) {
        if (data instanceof Appointment) {
            this.appointment = (Appointment) data;

            // Set text fields directly from the appointment data
            titleField.setText(appointment.getTitle());
            descriptionField.setText(appointment.getDescription());
            typeField.setText(appointment.getType());
            idField.setText(String.valueOf(appointment.getAppointmentID()));

            // Convert UTC start date and time to local date and time and set it in the respective fields
            LocalDateTime startDateUtc = appointment.getStartDate();
            ZonedDateTime utcStart = startDateUtc.atZone(ZoneId.of("UTC"));
            ZonedDateTime startZonedDateTimeLocal = utcStart.withZoneSameInstant(ZoneId.systemDefault());
            startDatePickerField.setValue(startZonedDateTimeLocal.toLocalDate());
            startTimeHour.setValue(String.format("%02d", startZonedDateTimeLocal.getHour()));
            startTimeMinutes.setValue(String.format("%02d", startZonedDateTimeLocal.getMinute()));

            // Convert UTC end date and time to local date and time and set it in the respective fields
            LocalDateTime endDateUtc = appointment.getEndDate();
            ZonedDateTime utcEnd = endDateUtc.atZone(ZoneId.of("UTC"));
            ZonedDateTime endZonedDateTimeLocal = utcEnd.withZoneSameInstant(ZoneId.systemDefault());
            endDatePicker.setValue(endZonedDateTimeLocal.toLocalDate());
            endTimeHour.setValue(String.format("%02d", endZonedDateTimeLocal.getHour()));
            endTimeMinutes.setValue(String.format("%02d", endZonedDateTimeLocal.getMinute()));

            // Set other appointment details in the respective fields
            locationField.setText(appointment.getLocation());
            customerIDComboField.setValue(appointment.getCustomerID());
            contactComboField.setValue(appointment.getContactID());
            userIDField.setValue(appointment.getUserID());
        }
    }

    /**
     * Handles the action triggered by clicking the 'Cancel' button. This method returns the user
     * to the appointments overview screen without making any changes to the data.
     *
     * @param actionEvent The event that triggered this action, typically the clicking of the 'Cancel' button.
     */
    public void onCancelButton(ActionEvent actionEvent) {
        // Load the Appointments view into the main panel, discarding any changes made.
        mainBorderPane.setCenter(loadView("/view/Appointments.fxml", rb));
    }

    /**
     * Handles the action triggered by clicking the 'Save' button. This method validates the form fields
     * and updates the appointment data if validation is successful. It also handles conversion of date
     * and time data from local time zone to UTC for consistent storage.
     *
     * If validation fails, the method will abort the saving process and potentially alert the user
     * to the validation issues.
     *
     * @param actionEvent The event that triggered this action, typically the clicking of the 'Save' button.
     */
    public void onSaveButton(ActionEvent actionEvent) {
        // Validate input fields first
        if (!areFieldsValid()) {
            return; // Exit the method early if validation fails
        }

        // Extract field values
        String title = titleField.getText();
        String description = descriptionField.getText();
        String type = typeField.getText();
        String location = locationField.getText();
        LocalDate start = startDatePickerField.getValue();
        LocalDate end = endDatePicker.getValue();
        String startHour = String.valueOf(startTimeHour.getValue());
        String startMinute = String.valueOf(startTimeMinutes.getValue());
        String endHour = String.valueOf(endTimeHour.getValue());
        String endMinute = String.valueOf(endTimeMinutes.getValue());
        int customerID = (int) customerIDComboField.getValue();
        int userID = (int) userIDField.getValue();
        int contactID = (int) contactComboField.getValue();
        int appointmentId = Integer.parseInt(idField.getText());

        // Prepare and convert date and time
        LocalTime startTime = LocalTime.of(Integer.parseInt(startHour), Integer.parseInt(startMinute));
        LocalTime endTime = LocalTime.of(Integer.parseInt(endHour), Integer.parseInt(endMinute));
        ZoneId defaultZoneId = ZoneId.systemDefault();
        ZonedDateTime startZonedDateTime = LocalDateTime.of(start, startTime).atZone(defaultZoneId).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime endZonedDateTime = LocalDateTime.of(end, endTime).atZone(defaultZoneId).withZoneSameInstant(ZoneId.of("UTC"));

        // Validate and save the appointment if valid
        if (validateAppointment(startZonedDateTime, endZonedDateTime, defaultZoneId, customerID, appointmentId)) {
            editAppointment(title, description, location, type, startZonedDateTime, endZonedDateTime, customerID, userID, contactID, appointmentId);
            mainBorderPane.setCenter(loadView("/view/Appointments.fxml", rb));
        }
    }

    /**
     * Validates the input fields within the form to ensure that no required fields are empty and that
     * all entered data adheres to logical constraints (e.g., end time is after start time).
     *
     * This method checks for empty fields and validates time consistency between start and end times,
     * providing user feedback through alerts if invalid data is found.
     *
     * @return boolean Returns true if all fields are valid, otherwise false if any validation check fails.
     */
    private boolean areFieldsValid() {
        // Check for empty fields across all inputs
        if (titleField.getText().trim().isEmpty() || descriptionField.getText().trim().isEmpty() ||
                locationField.getText().trim().isEmpty() || typeField.getText().trim().isEmpty() ||
                startDatePickerField.getValue() == null || endDatePicker.getValue() == null ||
                startTimeHour.getValue() == null || startTimeMinutes.getValue() == null ||
                endTimeHour.getValue() == null || endTimeMinutes.getValue() == null ||
                customerIDComboField.getValue() == null || userIDField.getValue() == null ||
                contactComboField.getValue() == null) {

            showAlert("Validation Error", "Please fill in all required fields.");
            return false;
        }

        // Validate that the end time is after the start time
        if (startTimeHour.getValue() != null && startTimeMinutes.getValue() != null &&
                endTimeHour.getValue() != null && endTimeMinutes.getValue() != null) {
            LocalTime startTime = LocalTime.of(Integer.parseInt(startTimeHour.getValue()), Integer.parseInt(startTimeMinutes.getValue()));
            LocalTime endTime = LocalTime.of(Integer.parseInt(endTimeHour.getValue()), Integer.parseInt(endTimeMinutes.getValue()));
            if (endTime.isBefore(startTime)) {
                showAlert("Time Error", "End time cannot be before start time.");
                return false;
            }
        }

        return true;
    }

    /**
     * Configures the date pickers in the form to enhance user experience and enforce logical constraints.
     * Specifically, past dates are disabled to prevent selection, and the end date picker is configured
     * to ensure that the end date is always after the start date.
     */
    private void configureDatePickers() {
        // Disable past dates in the start date picker and apply a grey background style for disabled dates
        startDatePickerField.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date == null || date.isBefore(LocalDate.now()));
                setStyle(date != null && date.isBefore(LocalDate.now()) ? "-fx-background-color: #D3D3D3;" : "");
            }
        });

        // Ensure that the selected end date is always after the selected start date
        endDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate startDate = startDatePickerField.getValue();
                setDisable(empty || date == null || date.isBefore(startDate) || date.isBefore(LocalDate.now()));
                setStyle(date != null && startDate != null && date.isBefore(startDate) ? "-fx-background-color: #D3D3D3;" : "");
            }
        });

        // Automatically adjust the end date when the start date changes to maintain logical consistency
        startDatePickerField.valueProperty().addListener((obs, oldDate, newDate) -> endDatePicker.setValue(newDate));
    }

}
