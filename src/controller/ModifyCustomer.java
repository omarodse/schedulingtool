package controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import model.Country;
import model.Customer;
import model.Division;

import java.net.URL;
import java.util.ResourceBundle;

import static DAO.CountryDAO.getAllCountries;
import static DAO.CustomerDAO.editCustomer;
import static DAO.DivisionDAO.getAllDivisions;
import static DAO.DivisionDAO.getDivisionsPerCountry;
import static utilities.ManageState.*;

/**
 * Controller for the Modify Customer GUI of the application.
 * <p>
 * This class is responsible for handling all user interactions on the Modify Customer GUI,
 * including populating the distinct combo boxes, and validating all inputs.
 */
public class ModifyCustomer implements Initializable, InitializableWithData {

    public TextField nameField;
    public TextField phoneField;
    public TextField addressField;
    public TextField zipCodeField;
    public ComboBox<Country> countryCombo;
    public ComboBox<Division> divisionCombo;
    public Button cancelButton;
    public Button saveButton;

    public TextField customerID;
    public Label divisionError;
    @FXML
    private BorderPane mainBorderPane = getMainBorderPane();
    private ResourceBundle rb = getRB();
    private Customer customer;

    /**
     * Initializes the controller by setting up the country and division combo boxes. It also sets up a listener
     * on the country combo box to update the division combo box based on the selected country. This method ensures
     * that the UI is correctly populated and responsive to user input, facilitating the modification of customer data.
     *
     * @param url The location used to resolve relative paths for the root object, or {@code null} if the location is not known.
     * @param resourceBundle The resources used to localize the root object, providing localized strings for UI components.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<Country> countries = getAllCountries();
        countryCombo.setItems(countries);

        // Set up the listener for the country combo box to update divisions based on the selected country
        countryCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                ObservableList<Division> divisions = getDivisionsPerCountry(newVal);
                divisionCombo.getItems().clear();
                divisionCombo.getItems().addAll(divisions);
                divisionCombo.getSelectionModel().clearSelection();

                // Set the division from existing customer data if available
                if (customer != null && customer.getDivision() != null) {
                    for (Division division : divisions) {
                        if (division.getDivision().equals(customer.getDivision())) {
                            divisionCombo.setValue(division);
                            break;
                        }
                    }
                }
            }
        });
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
     * Handles the click event of the cancel button, it cancels the action of editing a Customer,
     * and it navigates back to the customer list.
     * <p>
     * @param actionEvent The event triggered when the cancel button is pressed.
     */
    public void onCancelButton(ActionEvent actionEvent) {
        mainBorderPane.setCenter(loadView("/view/Customers.fxml", rb));
    }
    /**
     * Handles the save action triggered by clicking the 'Save' button on the customer modification form.
     * This method validates the selected division and country for consistency, then saves the updated customer
     * data to the database if all fields are properly filled and validation checks pass. It also handles navigation
     * back to the customer overview screen upon successful data submission.
     *
     * @param actionEvent The event that triggered this action, typically the clicking of the 'Save' button.
     */
    public void onSaveButton(ActionEvent actionEvent) {
        // Extract data from form fields
        String customerName = nameField.getText();
        String address = addressField.getText();
        String postalCode = zipCodeField.getText();
        String phone = phoneField.getText();
        int customerId = Integer.parseInt(customerID.getText());

        // Retrieve selected country and division from combo boxes
        Country country = countryCombo.getValue();
        Division division = divisionCombo.getValue();

        // Check if the division is selected and if it belongs to the selected country
        if (division == null) {
            showAlert("Validation Error", "Please select a division.");
            return; // Stop processing if no division is selected
        } else if (division.getCountryID() != country.getCountryID()) {
            divisionError.setVisible(true); // Show error if division does not match the country
            showAlert("Validation Error", "The selected division does not match the country.");
            return;
        }

        // Call the method to edit customer data in the database
        editCustomer(customerName, address, postalCode, phone, division.getDivisionID(), customerId);

        // Reload the Customers view into the main panel
        mainBorderPane.setCenter(loadView("/view/Customers.fxml", rb));
    }


    /**
     * Initializes the form fields with existing customer data when an instance of Customer is passed. This method
     * is responsible for populating the fields with data from the customer object, ensuring the form is ready for editing.
     *
     * @param data The customer data to be loaded into the form fields. This should be an instance of Customer.
     */
    public void initializeData(Object data) {
        if (data instanceof Customer) {
            this.customer = (Customer) data;

            nameField.setText(customer.getCustomerName());
            addressField.setText(customer.getAddress());
            phoneField.setText(customer.getPhone());
            zipCodeField.setText(customer.getPostalCode());
            customerID.setText(String.valueOf(customer.getCustomerID()));

            // Attempt to set the corresponding country and division based on existing customer data
            ObservableList<Division> divisions = getAllDivisions();
            ObservableList<Country> countries = getAllCountries();

            int countryID = 0;
            for (Division division : divisions) {
                if (division.getDivision().equals(customer.getDivision())) {
                    countryID = division.getCountryID();
                    // Intentionally do not set the division here to maintain country selection independence
                }
            }

            // Set the country after identifying the correct countryID
            for (Country country : countries) {
                if (country.getCountryID() == countryID) {
                    countryCombo.setValue(country);
                }
            }
        }
    }

}
