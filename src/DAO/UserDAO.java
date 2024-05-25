package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static utilities.DBConnection.getConnection;

/**
 * Handles database operations related to users within the application. This class provides
 * methods for user authentication and retrieving a list of user IDs. It is responsible for
 * validating user credentials and facilitating the management of user-related data.
 */
public class UserDAO {

    /**
     * Attempts to authenticate a user based on the provided username and password.
     * The method checks the database for a matching username and compares the stored
     * password with the provided one.
     *
     * @param userName The username provided by the user.
     * @param password The password provided by the user.
     * @return A boolean indicating whether the authentication was successful. Returns
     * true if the username and password match the database record, otherwise false.
     * @throws SQLException if there is an error during database access.
     */
    public static boolean userAccess(String userName, String password) throws SQLException {

        if (userName == null || userName.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return false;
        }

        String sqlQuery = "SELECT password FROM users WHERE User_Name = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlQuery)) {

            ps.setString(1, userName);
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    String storedPassword = resultSet.getString("password");
                    return storedPassword.equals(password);
                }
            }
        }
        return false;
    }

    /**
     * Retrieves a list of all user IDs from the database. This method queries the users
     * table to collect all existing user IDs and returns them in an ObservableList.
     * This list can be used for user management tasks where a complete list of users is required.
     *
     * @return An ObservableList containing integers, each representing a unique user ID
     * from the database.
     */
    public static ObservableList<Integer> getAllUserIDs() {
        ObservableList<Integer> userIDList = FXCollections.observableArrayList();

        try{
            String query = "SELECT User_ID FROM users";

            PreparedStatement ps = getConnection().prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                int userID = rs.getInt("User_ID");
                userIDList.add(userID);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userIDList;
    }
}
