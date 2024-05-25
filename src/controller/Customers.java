package controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import model.Customer;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static DAO.AppointmentDAO.deleteAllAppointmentsForCustomer;
import static DAO.CustomerDAO.deleteCustomerFromDB;
import static DAO.CustomerDAO.getAllCustomers;
import static utilities.ManageState.*;

/**
 * Controller for the Customer GUI of the application.
 * <p>
 * This class is responsible for handling all user interactions on the Customer GUI,
 * including initializing the Customer table. The TableView contains a column which has
 * a three dot button that was added programmatically to the table.
 */
public class Customers implements Initializable {
    public Button addCustomer;

    public TableColumn customerID;

    public TableColumn customerName;
    public TableColumn customerPhone;
    public TableColumn customerAddress;
    public TableColumn customerPostal;
    public TableColumn customerDivision;
    public TableView<Customer> customersTable;
    public TableColumn editCustomerButton;

    public BorderPane mainBorderPane;
    private ResourceBundle rb = getRB();
    private final Image image = new Image(getClass().getResourceAsStream("/resources/img.png"));

    private final ImageView imageView = new ImageView(image);

    @Override
    /**
     * Initializes the controller class for the customer management screen. This method configures the
     * customersTable to display data retrieved from the database, setting up column bindings and
     * loading all customers into the table. It also configures a custom options column for additional
     * actions like editing or deleting customers.
     *
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     * Initializes table columns with specific property values from the Customer model, ensuring each column
     * correctly displays the intended data field:
     * - customerID column displays the customer's ID.
     * - customerName column displays the customer's name.
     * - customerAddress column displays the customer's address.
     * - customerPostal column displays the customer's postal code.
     * - customerPhone column displays the customer's phone number.
     * - customerDivision column displays the customer's division.
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set the items of the customer table by fetching all customers from the database.
        customersTable.setItems(getAllCustomers());

        // Bind the table columns to the corresponding properties in the Customer model.
        customerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        customerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        customerPostal.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        customerPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        customerDivision.setCellValueFactory(new PropertyValueFactory<>("division"));

        // Setup additional options like edit and delete for each row in the table.
        setupOptionsColumn();
    }

    /**
     * Configures the options column in the customer table with buttons for each row.
     * These buttons are set up with icons and actions that open a context menu offering options to edit or delete the customer.
     * The method defines a cell factory for the table column that creates a new cell containing a button with a predefined icon.
     */
    private void setupOptionsColumn() {
        editCustomerButton.setCellFactory(new Callback<TableColumn<Customer, Void>, TableCell<Customer, Void>>() {
            @Override
            public TableCell<Customer, Void> call(final TableColumn<Customer, Void> param) {
                final TableCell<Customer, Void> cell = new TableCell<Customer, Void>() {
                    private final Button btn = new Button();
                    private final ImageView imgView = new ImageView(image);

                    {
                        imgView.setFitHeight(15);
                        imgView.setFitWidth(15);
                        btn.setGraphic(imgView);

                        btn.setOnAction(event -> {
                            Customer customer = getTableView().getItems().get(getIndex());
                            showOptionsMenu(customer, btn);
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
     * Displays a context menu for editing or deleting the specified customer.
     * The menu is triggered from a button within the customer table and provides the options "Edit" and "Delete".
     *
     * @param customer The customer instance associated with the selected row in the table.
     * @param button The button used to trigger the context menu, ensuring it appears adjacent to the button.
     */
    private void showOptionsMenu(Customer customer, Button button) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editItem = new MenuItem("Edit");
        MenuItem deleteItem = new MenuItem("Delete");
        contextMenu.getItems().addAll(editItem, deleteItem);

        editItem.setOnAction(e -> editCustomer(customer));
        deleteItem.setOnAction(e -> {
            try {
                deleteCustomer(customer);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        contextMenu.show(button, Side.BOTTOM, 0, 0);
    }

    /**
     * Initiates the process of editing the given customer's details.
     * This method displays the customer editing form in the central pane of the main application window.
     *
     * @param customer The customer to be edited.
     */
    private void editCustomer(Customer customer) {
        System.out.println("Editing: " + customer.getCustomerName());
        Node customerEditView = loadView("/view/ModifyCustomerForm.fxml", rb, customer);
        mainBorderPane.setCenter(customerEditView);
    }

    /**
     * Initiates the deletion of the specified customer after confirmation.
     * An alert dialog asks for confirmation, and if confirmed, the customer is deleted from the database,
     * and the customer view is refreshed.
     *
     * @param customer The customer to be deleted.
     */
    private void deleteCustomer(Customer customer) throws SQLException {
        if(customer != null) {
            String name = customer.getCustomerName();
            System.out.println("Deleting: " + name);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete " + name + "?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                deleteAllAppointmentsForCustomer(customer.getCustomerID());
                deleteCustomerFromDB(customer.getCustomerID());
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Deletion Successful");
                successAlert.setHeaderText(null);
                successAlert.setContentText("The customer '" + name + "' and related appointments were successfully deleted.");
                successAlert.showAndWait();
                mainBorderPane.setCenter(loadView("/view/Customers.fxml", rb));
            }
        }
    }

    /**
     * Handles the action triggered by the 'Add Customer' button.
     * This method loads the customer addition form into the central pane of the main application window.
     *
     * @param actionEvent The event triggered by the button click.
     */
    public void onAddCustomer(ActionEvent actionEvent) {
        mainBorderPane.setCenter(loadView("/view/AddCustomerForm.fxml", rb));
    }

}
