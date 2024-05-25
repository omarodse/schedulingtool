package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Customer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static utilities.DBConnection.getConnection;

public class CustomerDAO {


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
