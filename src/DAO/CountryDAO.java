package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Country;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static utilities.DBConnection.getConnection;

/**
 * This class manages the data access operations related to countries within the system.
 * It provides methods to retrieve all countries from the database, facilitating the integration
 * of country information into the application's user interfaces. The class leverages JDBC
 * for database interactions, with SQL queries executed to fetch necessary data.
 */
public class CountryDAO {

    /**
     * Retrieves all countries from the database and returns them as an ObservableList.
     * Each country's ID and name are fetched from the database, and these details are used
     * to create Country objects which are then accumulated into an ObservableList.
     * @return an ObservableList of Country objects containing details of all countries
     *         from the database. If an SQL exception occurs during the operation,
     *         the method prints the stack trace and returns an empty list.
     */
    public static ObservableList<Country> getAllCountries() {
        ObservableList<Country> countryList = FXCollections.observableArrayList();

        try {
            String query = "SELECT Country_ID, Country FROM countries";
            PreparedStatement ps = getConnection().prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int countryID = rs.getInt("Country_ID");
                String countryName = rs.getString("Country");

                Country country = new Country(countryID, countryName);
                countryList.add(country);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return countryList;
    }
}
