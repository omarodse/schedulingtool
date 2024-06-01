package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Customer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static utilities.DBConnection.getConnection;

/**
 * Handles database operations related to customers within the application. It provides
 * functionalities for creating, updating, deleting, and fetching customer records.
 * This class simplifies the interaction with the database by abstracting complex SQL
 * queries and providing a straightforward API for managing customer data.
 */
public class CustomerDAO {

    /**
     * Inserts a new customer into the database. The method constructs an SQL statement to insert
     * customer details and executes it using a PreparedStatement to protect against SQL injection.
     * @param Customer_Name The name of the customer.
     * @param Address The address of the customer.
     * @param Postal_Code The postal code of the customer's address.
     * @param Phone The phone number of the customer.
     * @param Division_ID The database ID of the division the customer belongs to.
     */
    public static void createCustomer(String Customer_Name, String Address, String Postal_Code, String Phone, int Division_ID) {
        PreparedStatement ps = null;

        try{
            String query = "INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Division_ID, Created_By, Last_Updated_By, Create_Date, Last_Update) " +
                           "VALUES (?, ?, ?, ?, ?, 'default_user', 'default_user', NOW(), NOW())";

            ps = getConnection().prepareStatement(query);

            ps.setString(1, Customer_Name);
            ps.setString(2, Address);
            ps.setString(3, Postal_Code);
            ps.setString(4, Phone);
            ps.setInt(5, Division_ID);

            int result = ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close(); // Close PreparedStatement
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Updates an existing customer's data in the database based on the customer ID. The method
     * allows modification of all customer details except for the customer ID, which is used to
     * uniquely identify the record to update.
     * @param Customer_Name The new name of the customer.
     * @param Address The new address of the customer.
     * @param Postal_Code The new postal code of the customer's address.
     * @param Phone The new phone number of the customer.
     * @param Division_ID The new division ID of the customer.
     * @param Customer_ID The ID of the customer to update.
     */
    public static void editCustomer(String Customer_Name, String Address, String Postal_Code, String Phone, int Division_ID, int Customer_ID) {

        try{
            String query = "UPDATE customers set Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Division_ID = ?, Last_Update = NOW() " +
                           "WHERE Customer_ID = ?";

            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setString(1, Customer_Name);
            ps.setString(2, Address);
            ps.setString(3, Postal_Code);
            ps.setString(4, Phone);
            ps.setInt(5, Division_ID);
            ps.setInt(6, Customer_ID);

            ps.execute();

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a customer from the database using the customer ID as a key.
     * @param Customer_ID The ID of the customer to delete.
     */
    public static void deleteCustomerFromDB(int Customer_ID) {

        try{
            String query = "DELETE from customers " +
                           "WHERE Customer_ID = ?";

            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setInt(1, Customer_ID);

            ps.execute();

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a list of all customers from the database. This method queries the database
     * for all records in the customers table, constructs Customer objects for each, and
     * returns them in an ObservableList.
     * @return An ObservableList containing Customer objects representing all customers
     * in the database.
     */
    public static ObservableList<Customer> getAllCustomers() {
        ObservableList<Customer> customerList = FXCollections.observableArrayList();

        try{
            String query = "SELECT Customer_ID, Customer_Name, Address, Postal_Code, Phone, Division " +
                           "FROM customers, first_level_divisions " +
                           "WHERE customers.Division_ID = first_level_divisions.Division_ID";

            PreparedStatement ps = getConnection().prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                int customerID = rs.getInt("Customer_ID");
                String customerName = rs.getString("Customer_Name");
                String address = rs.getString("Address");
                String postalCode = rs.getString("Postal_Code");
                String phone = rs.getString("Phone");
                String division = rs.getString("Division");

                Customer customer = new Customer(customerID, customerName, address, postalCode, phone, division);
                customerList.add(customer);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customerList;
    }

    /**
     * Fetches a list of all customer IDs from the database. This method is useful for
     * populating UI elements that require a list of customer IDs, or for validation purposes.
     * @return An ObservableList containing Integer objects, each representing a unique
     * customer ID from the database.
     */
    public static ObservableList<Integer> getAllCustomerIDs() {
        ObservableList<Integer> customerIDList = FXCollections.observableArrayList();

        try{
            String query = "SELECT Customer_ID FROM customers";

            PreparedStatement ps = getConnection().prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                int customerID = rs.getInt("Customer_ID");
                customerIDList.add(customerID);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customerIDList;
    }
}
