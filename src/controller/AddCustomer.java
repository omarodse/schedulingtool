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

/**
 * Controller for the Add Customer form of the application.
 * <p>
 * This class is responsible for handling all user interactions on the Add Customer form,
 * including initializing the distinct Combo Boxes, and validating the information.
 */
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

    /**
     * Initializes the controller, setting up the Combo Boxes.
     *
     * @param url The location used to resolve relative paths for the root object, or null if unknown.
     * @param resourceBundle The resources used to localize the root object, or null if not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        countryCombo.setItems(getAllCountries());
        divisionCombo.setItems(getAllDivisions());
    }

    /**
     * Handles the click event of the cancel button, it cancels the creation of a new Customer,
     * and navigates back to the customer list.
     * <p>
     * @param actionEvent The event triggered when the cancel button is pressed.
     */
    public void onCancelButton(ActionEvent actionEvent) {
        mainBorderPane.setCenter(loadView("/view/Customers.fxml", rb));
    }

    /**
     * Handles the click event of the combo box for countries, and it populates the correspondent
     * divisions(cities/provinces) per country.
     * <p>
     * @param actionEvent The event triggered when the country combo box is clicked.
     */
    public void onCountryCombo(ActionEvent actionEvent) {
        Country country = countryCombo.getValue();
        if(country != null) {
            divisionCombo.setItems(getDivisionsPerCountry(country));
        }
    }

    /**
     * Handles saving a new Customer.
     * <p>
     * Validates user input and creates a new Customer. Displays confirmation or error messages as appropriate.
     * @param actionEvent The event triggered when the save button is pressed.
     */
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

    /**
     * Handles validation for all fields.
     * <p>
     * Validates user input. Displays confirmation or error messages as appropriate.
     * @return true if the values are valid, false otherwise.
     */
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
