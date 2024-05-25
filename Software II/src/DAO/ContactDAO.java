package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Contact;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static utilities.DBConnection.getConnection;

public class ContactDAO {

    public static ObservableList<Contact> getAllContacts() {
        ObservableList<Contact> contactList = FXCollections.observableArrayList();

        try{
            String query = "SELECT Contact_ID, Contact_Name FROM contacts";

            PreparedStatement ps = getConnection().prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
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
}
