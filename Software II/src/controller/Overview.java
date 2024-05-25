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

public class Overview implements Initializable, InitializableWithData {

    @FXML
    public BorderPane mainBorderPane;
    @FXML
    public Button customersButton;
    @FXML
    public Button overviewButton;
    @FXML
    public Button appointmentsButton;
    public Button reportsButton;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        contactComboBox.setItems(getAllContacts());
        appointmentsTomorrow.setText(String.valueOf(getCountOfAppointmentsForNextDay()));
        Appointment upcomingAppointment;

        try {
            upcomingAppointment = getUpcomingAppointmentForUser(ZoneId.systemDefault());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        displayUpcomingAppointment(upcomingAppointment);

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

    @Override
    public void initializeData(Object data) {

    }

    public void displayUpcomingAppointment(Appointment appointment) {
        if (appointment != null) {
            String appointmentID = String.valueOf(appointment.getAppointmentID());
            String startDate = String.valueOf(appointment.getStartDate());
            nextAppointment.setText("Appointment ID: " + appointmentID + "\n\nStart Date: " + startDate);
        }
    }

    public void onCustomers(ActionEvent actionEvent) {
        overviewButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgba(255, 255, 255, 0.6);");
        appointmentsButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgba(255, 255, 255, 0.6);");
        customersButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgba(255, 255, 255, 1);");
        mainBorderPane.setCenter(loadView("/view/Customers.fxml", rb));
    }

    public void onOverview(ActionEvent actionEvent) {
        customersButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgba(255, 255, 255, 0.6);");
        overviewButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgba(255, 255, 255, 1);");
        appointmentsButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgba(255, 255, 255, 0.6);");
        mainBorderPane.setCenter(loadView("/view/Overview.fxml", rb));
    }

    public void onAppointments(ActionEvent actionEvent) {
        customersButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgba(255, 255, 255, 0.6);");
        overviewButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgba(255, 255, 255, 0.6);");
        appointmentsButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgba(255, 255, 255, 1);");
        mainBorderPane.setCenter(loadView("/view/Appointments.fxml", rb));
    }

    public void onContactCombo(ActionEvent actionEvent) {
        Contact contact = contactComboBox.getValue();
        if(contact != null) {
            appointmentsPerContact.setItems(getAppointmentsPerContact(contact));
        }
    }

    public void onAddButton(MouseEvent mouseEvent) {
        mainBorderPane.setCenter(loadView("/view/AddAppointmentForm.fxml", rb));
    }
}
