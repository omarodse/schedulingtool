package controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;

import controller.MainScreen;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import model.Customer;
import utilities.ManageState;

import java.net.URL;
import java.util.ResourceBundle;

import static DAO.CustomerDAO.deleteCustomerFromDB;
import static DAO.CustomerDAO.getAllCustomers;
import static utilities.ManageState.*;

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
    private MainScreen mainScreen;

    public BorderPane mainBorderPane;
    private ResourceBundle rb = getRB();
    private final Image image = new Image(getClass().getResourceAsStream("/resources/img.png"));

    private final ImageView imageView = new ImageView(image);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customersTable.setItems(getAllCustomers());

        customerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        customerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        customerPostal.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        customerPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        customerDivision.setCellValueFactory(new PropertyValueFactory<>("division"));
        setupOptionsColumn();
    }

    public void setMainScreenController(MainScreen controller) {
        this.mainScreen = controller;
    }

    private void setupOptionsColumn() {
        editCustomerButton.setCellFactory(new Callback<TableColumn<Customer, Void>, TableCell<Customer, Void>>() {
            @Override
            public TableCell<Customer, Void> call(final TableColumn<Customer, Void> param) {
                final TableCell<Customer, Void> cell = new TableCell<Customer, Void>() {
                    private final Button btn = new Button();
                    private final Image img = new Image(getClass().getResourceAsStream("/resources/img.png"));
                    private final ImageView imgView = new ImageView(img);

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

    private void showOptionsMenu(Customer customer, Button button) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editItem = new MenuItem("Edit");
        MenuItem deleteItem = new MenuItem("Delete");
        contextMenu.getItems().addAll(editItem, deleteItem);

        editItem.setOnAction(e -> editCustomer(customer));
        deleteItem.setOnAction(e -> deleteCustomer(customer));

        contextMenu.show(button, Side.BOTTOM, 0, 0);
    }

    private void editCustomer(Customer customer) {
        System.out.println("Editing: " + customer.getCustomerName());
        Node customerEditView = loadView("/view/ModifyCustomerForm.fxml", rb, customer);
        mainBorderPane.setCenter(customerEditView);
    }

    private void deleteCustomer(Customer customer) {
        String name = customer.getCustomerName();
        System.out.println("Deleting: " + name);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete " + name + "?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            deleteCustomerFromDB(customer.getCustomerID());
            mainBorderPane.setCenter(loadView("/view/Customers.fxml", rb));
        }
    }

    public void onAddCustomer(ActionEvent actionEvent) {
        mainBorderPane.setCenter(loadView("/view/AddCustomerForm.fxml", rb));
    }
}
