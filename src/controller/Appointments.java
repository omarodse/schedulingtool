package controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import model.Appointment;

import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ResourceBundle;

import static DAO.AppointmentDAO.*;
import static utilities.ManageState.*;

/**
 * Controller for the Appointment GUI of the application.
 * <p>
 * This class is responsible for handling all user interactions on the Appointment GUI,
 * including initializing its Appointment tableView. The TableView contains a column which has
 * a three dot button that was added programmatically to the table.
 */
public class Appointments implements Initializable {
    public TableColumn appointmentID;

    public TableColumn title;
    public TableColumn description;

    public TableColumn location;
    public TableColumn contactID;
    public TableColumn type;
    public TableColumn<Appointment, LocalDateTime> startDate;
    public TableColumn<Appointment, LocalDateTime> endDate;

    public TableColumn customerID;
    public TableColumn userID;
    public TableView<Appointment> appointmentsTable;
    public TableColumn optionsButton;
    public Button addAppointmentButton;
    public TabPane tabPane;
    public Tab weekView;
    public Tab monthView;
    public Label monthYear;
    public ComboBox<Integer> monthComboBox;
    public ComboBox<String> typeComboBox;
    public Button fetchButton;
    public Label numberOfAppointments;
    @FXML
    private BorderPane mainBorderPane = getMainBorderPane();
    private final ResourceBundle rb = getRB();
    private final Image image = new Image(getClass().getResourceAsStream("/resources/img.png"));

    private final ImageView imageView = new ImageView(image);

    /**
     * Initializes the controller class for the main application screen.
     * This method sets up the data bindings for table columns, configures custom cell factories,
     * populates combo boxes, and sets up listeners for UI components.
     * It also formats and displays the current date in the 'monthYear' label.
     * Specifically, this method:
     * - Loads numeric representations for months.
     * - Sets items in the 'typeComboBox' from fetched appointment types.
     * - Populates 'appointmentsTable' with all appointments.
     * - Configures each column in 'appointmentsTable' to display appropriate fields from the Appointment model.
     * - Sets custom cell factories for 'startDate' and 'endDate' columns to convert UTC LocalDateTime to the system default timezone.
     * - Listens for changes in the selected tab to update the appointments table view accordingly.
     * - Loads month-specific data and sets up options column interactions.
     * - Adds a three dot button programmatically on the last column of the table with the method 'setupOptionsColumn()'.
     *
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadMonthNumbers();
        typeComboBox.setItems(fetchAppointmentTypes());
        appointmentsTable.setItems(getAllAppointments());

        appointmentID.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));
        location.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactID.setCellValueFactory(new PropertyValueFactory<>("contactID"));
        type.setCellValueFactory(new PropertyValueFactory<>("type"));

        startDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        startDate.setCellFactory(column -> new TableCell<>() {
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

        endDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        endDate.setCellFactory(column -> new TableCell<>() {
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
        userID.setCellValueFactory(new PropertyValueFactory<>("userID"));

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        String formattedDate = currentDate.format(dateFormatter);
        monthYear.setText(formattedDate);

        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            System.out.println("Tab selected");
            if (newTab != null) {
                updateTableView(LocalDate.now());
            }
        });

        loadMonthData(LocalDate.now());
        setupOptionsColumn();
    }

    /**
     * Updates the content of the appointments table based on the currently selected tab in the tab pane.
     * This method determines which data set to load (either week or month view) by checking the currently selected tab
     * and then calling the appropriate method to load data for that specific time frame.
     *
     * @param referenceDate The date used as the starting point to determine the data range for loading appointments.
     *                      This date is typically the current date or a selected date from a calendar UI component.
     */
    private void updateTableView(LocalDate referenceDate) {
        if (tabPane.getSelectionModel().getSelectedItem() == weekView) {
            loadWeekData(referenceDate);
        } else if (tabPane.getSelectionModel().getSelectedItem() == monthView) {
            loadMonthData(referenceDate);
        }
    }

    /**
     * Loads and displays appointments for a given week into the appointments table.
     * This method calculates the start and end of the week based on the provided reference date,
     * fetches appointments for this period, and then updates the table view to reflect this data.
     *
     * @param referenceDate The date from which the week's start (Monday) and end (Sunday) are calculated.
     *                      The reference date is adjusted to the previous or the same Monday, ensuring the week
     *                      starts from Monday regardless of the given date.
     */
    private void loadWeekData(LocalDate referenceDate) {
        LocalDate startOfWeek = referenceDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        ObservableList<Appointment> weekAppointments = fetchAppointments(startOfWeek, endOfWeek);
        appointmentsTable.setItems(weekAppointments);
    }

    /**
     * Loads and displays appointments for the entire month based on a given reference date into the appointments table.
     * This method identifies the start and end of the month using the provided reference date, retrieves the appointments
     * for this time frame, and updates the table view to display these appointments.
     *
     * @param referenceDate The date used to determine the month for which appointments will be loaded. This date
     *                      helps in calculating the first and last day of the month.
     */
    private void loadMonthData(LocalDate referenceDate) {
        YearMonth yearMonth = YearMonth.from(referenceDate);
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();
        ObservableList<Appointment> monthAppointments = fetchAppointments(startOfMonth, endOfMonth);
        appointmentsTable.setItems(monthAppointments);
    }

    /**
     * Configures the options column in the appointments table by setting a custom cell factory.
     * Each cell in this column contains a button equipped with an icon, which when clicked,
     * triggers a context menu or a similar action related to the specific appointment in that row.
     *
     * The lambda allows for direct access to the button's event without additional code, enabling
     * the setup of the event handler inline where the button is instantiated. This usage enhances readability
     * and reduces the complexity of the event handling setup.
     */
    private void setupOptionsColumn() {
        optionsButton.setCellFactory(new Callback<TableColumn<Appointment, Void>, TableCell<Appointment, Void>>() {
            @Override
            public TableCell<Appointment, Void> call(final TableColumn<Appointment, Void> param) {
                final TableCell<Appointment, Void> cell = new TableCell<Appointment, Void>() {
                    private final Button btn = new Button();
                    private final Image img = new Image(getClass().getResourceAsStream("/resources/img.png"));
                    private final ImageView imgView = new ImageView(img);

                    {
                        imgView.setFitHeight(15);
                        imgView.setFitWidth(15);
                        btn.setGraphic(imgView);

                        btn.setOnAction(event -> {
                            Appointment appointment = getTableView().getItems().get(getIndex());
                            showOptionsMenu(appointment, btn);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        });
    }

    /**
     * Displays a context menu for each appointment with options to edit or delete the appointment.
     * The menu is shown when the user interacts with the button in the options column.
     *
     * @param appointment The appointment associated with the particular table row, providing the context for actions.
     * @param button The button tied to each table cell in the options column, used as an anchor for displaying the context menu.
     */
    private void showOptionsMenu(Appointment appointment, Button button) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editItem = new MenuItem("Edit");
        MenuItem deleteItem = new MenuItem("Cancel");
        contextMenu.getItems().addAll(editItem, deleteItem);

        // Set actions for menu items
        editItem.setOnAction(e -> editAppointment(appointment));
        deleteItem.setOnAction(e -> deleteAppointment(appointment));

        // Show the context menu adjacent to the button, appearing below it.
        contextMenu.show(button, Side.BOTTOM, 0, 0);
    }

    /**
     * Initiates the editing process for a given appointment. This method checks if the appointment
     * is in the past, and if so, it prevents editing by displaying an alert. If the appointment is
     * valid for editing, it loads the modification view into the main application area.
     *
     * @param appointment The appointment to be edited, containing all relevant details.
     */
    private void editAppointment(Appointment appointment) {
        // Check if the appointment end date is in the past
        if(appointment.getEndDate().isBefore(LocalDateTime.now())) {
            showAlert("Validation Error", "Past appointments cannot be edited.");
            return;
        }

        // Log the editing action
        System.out.println("Editing: " + appointment.getTitle());

        // Load the appointment modification view and display it in the mainBorderPane
        Node appointmentEditView = loadView("/view/ModifyAppointmentForm.fxml", rb, appointment);
        mainBorderPane.setCenter(appointmentEditView);
    }

    /**
     * Cancels an appointment in the database and provides feedback to the user.
     * This method presents a confirmation dialog, and if confirmed, it proceeds to delete
     * the appointment from the DB and then shows a custom message with the appointment details.
     *
     * @param appointment The appointment to be canceled.
     */
    private void deleteAppointment(Appointment appointment) {
        String appointmentDetails = "Appointment ID: " + appointment.getAppointmentID() +
                ", Type: " + appointment.getType();
        System.out.println("Canceling: " + appointmentDetails);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to cancel this appointment?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();  // Display the alert and wait for user response

        // Check user response and proceed if confirmed
        if (alert.getResult() == ButtonType.YES) {
            deleteAppointmentFromDB(appointment.getAppointmentID());  // Delete the appointment from the database
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Cancellation Successful");
            successAlert.setHeaderText(null);
            successAlert.setContentText("The appointment has been successfully canceled.\n" + appointmentDetails);
            successAlert.showAndWait();
            mainBorderPane.setCenter(loadView("/view/Appointments.fxml", rb));  // Refresh the main appointment view
        }
    }

    /**
     * Handles the action triggered by clicking the 'Add Appointment' button. This method sets the center of the main border pane
     * to the form for adding a new appointment, allowing users to input details for a new appointment.
     *
     * @param actionEvent The event that triggered this action, typically a button click.
     */
    public void onAddAppointmentButton(ActionEvent actionEvent) {
        mainBorderPane.setCenter(loadView("/view/AddAppointmentForm.fxml", rb));
    }

    /**
     * Handles the fetch action on a UI button to retrieve the number of appointments based on selected month and type.
     * Validates that both month and type have been selected before proceeding. If either is not selected,
     * it displays a validation error. If valid, it fetches and displays the count of appointments for the selected
     * criteria.
     *
     * @param actionEvent The event triggered by clicking the fetch button.
     */
    public void onFetchButton(ActionEvent actionEvent) {
        // Validate that both month and type selections are made
        if (monthComboBox.getSelectionModel().getSelectedItem() == null || typeComboBox.getSelectionModel().getSelectedItem() == null) {
            showAlert("Validation Error", "Both fields are required.");
            return;
        }

        // Retrieve selections from combo boxes
        int month = monthComboBox.getValue();
        String type = typeComboBox.getValue();

        // Fetch and display the number of appointments based on the type and month
        numberOfAppointments.setText(String.valueOf(countAppointmentsByTypeAndMonth(type, month)));
    }

    /**
     * Populates the monthComboBox with month numbers ranging from 1 to 12.
     * This method is typically called during the initialization of the UI to ensure
     * that the monthComboBox is fully populated for user selection.
     */
    private void loadMonthNumbers() {
        for (int month = 1; month <= 12; month++) {
            monthComboBox.getItems().add(month);
        }
    }

}
