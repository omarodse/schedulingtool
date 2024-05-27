package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Contact;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static utilities.DBConnection.getConnection;

/**
 * This class handles database operations related to contacts within the system.
 * It provides functionalities to retrieve all contacts from the database. The class utilizes JDBC to connect
 * to the database and execute SQL queries, ensuring proper management of resources with try-catch blocks
 * and finally clauses.
 */
public class ContactDAO {

    /**
     * Retrieves all contacts from the database and returns them as an ObservableList.
     * This method fetches the contact ID and name for each contact in the database and encapsulates
     * them into Contact objects which are then collected into an ObservableList.
     *
     * @return an ObservableList of Contact objects containing all contacts from the database.
     *         If an SQL exception occurs, the method prints the stack trace and returns an empty list.
     */
    public static ObservableList<Contact> getAllContacts() {
        ObservableList<Contact> contactList = FXCollections.observableArrayList();

        try {
            String query = "SELECT Contact_ID, Contact_Name FROM contacts";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int contactID = rs.getInt("Contact_ID");
                String contactName = rs.getString("Contact_Name");

                Contact contact = new Contact(contactID, contactName);
                contactList.add(contact);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contactList;
    }

    /**
     * Retrieves all contact IDs from the database and returns them as an ObservableList.
     * This method fetches the contact IDs for each contact in the database and encapsulates
     * them into Contact objects which are then collected into an ObservableList.
     *
     * @return an ObservableList of contact IDs containing all contact IDs from the database.
     *         If an SQL exception occurs, the method prints the stack trace and returns an empty list.
     */
    public static ObservableList<Integer> getAllContactIDs() {
        ObservableList<Integer> contactIDList = FXCollections.observableArrayList();

        try{
            String query = "SELECT Contact_ID FROM contacts";

            PreparedStatement ps = getConnection().prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                int contactID = rs.getInt("Contact_ID");
                contactIDList.add(contactID);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contactIDList;
    }
}