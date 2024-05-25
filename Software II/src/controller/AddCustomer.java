package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import model.Country;
import model.Division;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ResourceBundle;

import static DAO.CountryDAO.getAllCountries;
import static DAO.CustomerDAO.createCustomer;
import static DAO.DivisionDAO.getAllDivisions;
import static DAO.DivisionDAO.getDivisionsPerCountry;
import static utilities.ManageState.*;

public class AddCustomer implements Initializable {
    public Button cancelButton;
    public ComboBox<Country> countryCombo;
    public ComboBox<Division> divisionCombo;
    public Button saveButton;
    public TextField nameField;
    public TextField phoneField;
    public TextField addressField;
    public TextField zipCodeField;
    @FXML
    private BorderPane mainBorderPane = getMainBorderPane();
    private ResourceBundle rb = getRB();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        countryCombo.setItems(getAllCountries());
        divisionCombo.setItems(getAllDivisions());
    }

    public void onCancelButton(ActionEvent actionEvent) {
        mainBorderPane.setCenter(loadView("/view/Customers.fxml", rb));
    }

    public void onCountryCombo(ActionEvent actionEvent) {
        Country country = countryCombo.getValue();
        if(country != null) {
            divisionCombo.setItems(getDivisionsPerCountry(country));
        }
    }

    public void onSaveButton(ActionEvent actionEvent) {
        if(!areFieldsValid()) {
            return;
        }

        String customerName = nameField.getText();
        String address = addressField.getText();
        String postalCode = zipCodeField.getText();
        String phone = phoneField.getText();

        Division division = divisionCombo.getValue();
        if(division == null) {
            return;
        }

        createCustomer(customerName, address, postalCode, phone, division.getDivisionID());

        mainBorderPane.setCenter(loadView("/view/Customers.fxml", rb));
    }

    private boolean areFieldsValid() {
        // Check for empty text fields
        if (nameField.getText().trim().isEmpty() || phoneField.getText().trim().isEmpty() ||
                addressField.getText().trim().isEmpty() || zipCodeField.getText().trim().isEmpty() ||
                (countryCombo.getSelectionModel().getSelectedItem() == null) ||
                (divisionCombo.getSelectionModel().getSelectedItem() == null)) {

            showAlert("Validation Error", "Please fill in all required fields.");
            return false;
        }
        return true;
    }
}
