package controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import model.Contact;

import java.net.URL;
import java.time.*;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static DAO.AppointmentDAO.createAppointment;
import static DAO.ContactDAO.getAllContacts;
import static DAO.CustomerDAO.getAllCustomerIDs;
import static DAO.UserDAO.getAllUserIDs;
import static utilities.ManageState.*;

public class AddAppointment implements Initializable {
    public TextField titleField;
    public TextField descriptionField;
    public TextField typeField;
    public TextField idField;

    public BorderPane mainBorderPane;
    public DatePicker startDatePickerField;
    public TextField locationField;
    public ComboBox<Contact> contactComboField;
    public DatePicker endDatePicker;
    public ComboBox customerIDComboField;
    public ComboBox userIDField;
    public Button cancelButton;
    public Button saveButton;
    public ComboBox<String> startTimeHour;
    public ComboBox<String> startTimeMinutes;
    public ComboBox<String> endTimeHour;
    public ComboBox<String> endTimeMinutes;
    private ResourceBundle rb = getRB();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureDatePickers();

        startTimeHour.getItems().addAll(IntStream.rangeClosed(0, 23).mapToObj(i -> String.format("%02d", i)).collect(Collectors.toList()));
        startTimeMinutes.getItems().addAll(IntStream.rangeClosed(0, 59).mapToObj(i -> String.format("%02d", i)).collect(Collectors.toList()));

        startTimeHour.setValue("12");
        startTimeMinutes.setValue("00");

        endTimeHour.getItems().addAll(IntStream.rangeClosed(0, 23).mapToObj(i -> String.format("%02d", i)).collect(Collectors.toList()));
        endTimeMinutes.getItems().addAll(IntStream.rangeClosed(0, 59).mapToObj(i -> String.format("%02d", i)).collect(Collectors.toList()));

        endTimeHour.setValue("12");
        endTimeMinutes.setValue("00");

        contactComboField.setItems(getAllContacts());
        userIDField.setItems(getAllUserIDs());
        customerIDComboField.setItems(getAllCustomerIDs());
    }

    public void onCancelButton(ActionEvent actionEvent) {
        mainBorderPane.setCenter(loadView("/view/Appointments.fxml", rb));
    }

    public void onSaveButton(ActionEvent actionEvent) {
        if(!areFieldsValid()) {
            return;
        }

        String title = titleField.getText();
        String description = descriptionField.getText();
        String type = typeField.getText();
        String location = locationField.getText();
        LocalDate start = startDatePickerField.getValue();
        LocalDate end = endDatePicker.getValue();
        String startHour = startTimeHour.getValue();
        String startMinute = startTimeMinutes.getValue();
        String endHour = endTimeHour.getValue();
        String endMinute = endTimeMinutes.getValue();
        int customerID = (int) customerIDComboField.getValue();
        int userID = (int) userIDField.getValue();
        Contact contact = contactComboField.getValue();


        LocalTime startTime = LocalTime.of(Integer.parseInt(startHour), Integer.parseInt(startMinute));
        LocalTime endTime = LocalTime.of(Integer.parseInt(endHour), Integer.parseInt(endMinute));

        ZoneId defaultZoneId = ZoneId.systemDefault();
        ZonedDateTime startZonedDateTime = LocalDateTime.of(start, startTime).atZone(defaultZoneId).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime endZonedDateTime = LocalDateTime.of(end, endTime).atZone(defaultZoneId).withZoneSameInstant(ZoneId.of("UTC"));

        if (validateAppointment(startZonedDateTime, endZonedDateTime, defaultZoneId, customerID)) {
            createAppointment(title, description, location, type, startZonedDateTime, endZonedDateTime, customerID, userID, contact.getContactID());
            mainBorderPane.setCenter(loadView("/view/Appointments.fxml", rb));
        } else {
            // Optionally handle validation failure
        }
    }

    private boolean areFieldsValid() {
        // Check for empty text fields
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

        // Additional specific checks can be added here, for example:
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

    private void configureDatePickers() {
        // Disable past dates for start date picker
        startDatePickerField.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date == null || date.isBefore(LocalDate.now()));
                setStyle(date != null && date.isBefore(LocalDate.now()) ? "-fx-background-color: #D3D3D3;" : "");
            }
        });

        // Ensure the end date is after the start date
        endDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate startDate = startDatePickerField.getValue();
                setDisable(empty || date == null || date.isBefore(LocalDate.now()));
                setStyle(date != null && startDate != null && date.isBefore(startDate) ? "-fx-background-color: #D3D3D3;" : "");
            }
        });

        // Update end date restrictions when start date changes
        startDatePickerField.valueProperty().addListener((obs, oldDate, newDate) -> endDatePicker.setValue(newDate));
    }

}
