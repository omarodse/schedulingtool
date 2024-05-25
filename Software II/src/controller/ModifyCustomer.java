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
import static DAO.CustomerDAO.createCustomer;
import static DAO.CustomerDAO.editCustomer;
import static DAO.DivisionDAO.getAllDivisions;
import static DAO.DivisionDAO.getDivisionsPerCountry;
import static utilities.ManageState.*;

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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<Country> countries = getAllCountries();
        countryCombo.setItems(countries);

        // Set up the listener for the country combo box
        countryCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                ObservableList<Division> divisions = getDivisionsPerCountry(newVal);
                divisionCombo.getItems().clear();
                divisionCombo.getItems().addAll(divisions);
                divisionCombo.getSelectionModel().clearSelection();

                // Now set the division if appropriate
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

        // If customer is already set, you can call initializeData here or ensure it's called after initialization
        // initializeData(customer); // Uncomment if customer is already available
    }
    public void onCountryCombo(ActionEvent actionEvent) {
        Country country = countryCombo.getValue();
        if(country != null) {
            divisionCombo.setItems(getDivisionsPerCountry(country));
        }
    }
    public void onCancelButton(ActionEvent actionEvent) {
        mainBorderPane.setCenter(loadView("/view/Customers.fxml", rb));
    }
    public void onSaveButton(ActionEvent actionEvent) {

        String customerName = nameField.getText();
        String address = addressField.getText();
        String postalCode = zipCodeField.getText();
        String phone = phoneField.getText();
        int customerId = Integer.parseInt(customerID.getText());

        Country country = countryCombo.getValue();
        Division division = divisionCombo.getValue();
        if(division == null) {
            return;
        } else if(division.getCountryID() != country.getCountryID()) {
            divisionError.setVisible(true);
            return;
        }

        editCustomer(customerName, address, postalCode, phone, division.getDivisionID(), customerId);

        mainBorderPane.setCenter(loadView("/view/Customers.fxml", rb));
    }

    public void initializeData(Object data) {
        if (data instanceof Customer) {
            this.customer = (Customer) data;

            nameField.setText(customer.getCustomerName());
            addressField.setText(customer.getAddress());
            phoneField.setText(customer.getPhone());
            zipCodeField.setText(customer.getPostalCode());
            customerID.setText(String.valueOf(customer.getCustomerID()));

            ObservableList<Division> divisions = getAllDivisions();
            ObservableList<Country> countries = getAllCountries();

            int countryID = 0;
            for (Division division : divisions) {
                if (division.getDivision().equals(customer.getDivision())) {
                    countryID = division.getCountryID();
                    // Do not set the division here; just find the countryID
                }
            }

            // Set country based on countryID found
            for (Country country : countries) {
                if (country.getCountryID() == countryID) {
                    countryCombo.setValue(country);
                    // Setting the country will trigger the listener to update divisions
                }
            }

            // Set the division combo box in the listener or after the countries have been updated
        }
    }

}
