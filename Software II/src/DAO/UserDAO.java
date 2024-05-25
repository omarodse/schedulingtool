package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Contact;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static utilities.DBConnection.getConnection;

public class UserDAO {
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
