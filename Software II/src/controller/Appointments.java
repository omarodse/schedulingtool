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
    private MainScreen mainScreen;
    @FXML
    private BorderPane mainBorderPane = getMainBorderPane();
    private ResourceBundle rb = getRB();
    private final Image image = new Image(getClass().getResourceAsStream("/resources/img.png"));

    private final ImageView imageView = new ImageView(image);

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


    public void setMainScreenController(MainScreen controller) {
        this.mainScreen = controller;
    }

    private void updateTableView(LocalDate referenceDate) {
        if (tabPane.getSelectionModel().getSelectedItem() == weekView) {
            loadWeekData(referenceDate);
        } else if (tabPane.getSelectionModel().getSelectedItem() == monthView) {
            loadMonthData(referenceDate);
        }
    }

    private void loadWeekData(LocalDate referenceDate) {
        LocalDate startOfWeek = referenceDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        ObservableList<Appointment> weekAppointments = fetchAppointments(startOfWeek, endOfWeek);
        appointmentsTable.setItems(weekAppointments);
    }

    private void loadMonthData(LocalDate referenceDate) {
        YearMonth yearMonth = YearMonth.from(referenceDate);
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();
        ObservableList<Appointment> monthAppointments = fetchAppointments(startOfMonth, endOfMonth);
        appointmentsTable.setItems(monthAppointments);
    }

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

    private void showOptionsMenu(Appointment appointment, Button button) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editItem = new MenuItem("Edit");
        MenuItem deleteItem = new MenuItem("Delete");
        contextMenu.getItems().addAll(editItem, deleteItem);

        editItem.setOnAction(e -> editAppointment(appointment));
        deleteItem.setOnAction(e -> deleteAppointment(appointment));

        contextMenu.show(button, Side.BOTTOM, 0, 0);
    }

    private void editAppointment(Appointment appointment) {
        if(appointment.getEndDate().isBefore(LocalDateTime.now())) {
            showAlert("Validation Error", "Past appointments cannot be edited.");
            return;
        }
        System.out.println("Editing: " + appointment.getTitle());
        Node appointmentEditView = loadView("/view/ModifyAppointmentForm.fxml", rb, appointment);
        mainBorderPane.setCenter(appointmentEditView);
    }

    private void deleteAppointment(Appointment appointment) {
        String name = appointment.getTitle();
        System.out.println("Deleting: " + name);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete " + name + "?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            deleteAppointmentFromDB(appointment.getAppointmentID());
            mainBorderPane.setCenter(loadView("/view/Appointments.fxml", rb));
        }
    }

    public void onAddAppointmentButton(ActionEvent actionEvent) {
        mainBorderPane.setCenter(loadView("/view/AddAppointmentForm.fxml", rb));
    }

    public void onFetchButton(ActionEvent actionEvent) {
        if(monthComboBox.getSelectionModel().getSelectedItem() == null || typeComboBox.getSelectionModel().getSelectedItem() == null) {
            showAlert("Validation Error", "Both fields are required.");
            return;
        }
        int month = monthComboBox.getValue();
        String type = typeComboBox.getValue();

        numberOfAppointments.setText(String.valueOf(countAppointmentsByTypeAndMonth(type, month)));
    }

    private void loadMonthNumbers() {
        for (int month = 1; month <= 12; month++) {
            monthComboBox.getItems().add(month);
        }
    }

}
