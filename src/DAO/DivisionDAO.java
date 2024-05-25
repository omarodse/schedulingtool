package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Country;
import model.Division;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static utilities.DBConnection.getConnection;

/**
 * Handles database operations related to divisions within the application. This class provides
 * methods to retrieve divisions either globally or specific to a country, facilitating the management
 * of division-related data that is essential for addressing and other geographical classifications.
 */
public class DivisionDAO {

    /**
     * Retrieves all divisions from the database. This method queries the database
     * for all records in the first_level_divisions table, constructs Division objects
     * for each record, and returns them in an ObservableList.
     *
     * @return An ObservableList containing Division objects for all divisions found
     * in the database.
     */
    public static ObservableList<Division> getAllDivisions() {
        ObservableList<Division> divisionList = FXCollections.observableArrayList();

        try{
            String query = "SELECT Division_ID, Division, Country_ID FROM first_level_divisions";

            PreparedStatement ps = getConnection().prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                int divisionID = rs.getInt("Division_ID");
                String division = rs.getString("Division");
                int countryID = rs.getInt("Country_ID");

                Division divisionItem = new Division(divisionID, division, countryID);
                divisionList.add(divisionItem);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return divisionList;
    }

    /**
     * Fetches divisions based on the country specified. This method allows filtering of
     * divisions within a specific country, useful for contextual user interfaces where
     * divisions need to be displayed according to the selected country.
     *
     * @param country The country for which divisions are to be fetched.
     * @return An ObservableList containing Division objects that belong to the specified
     * country.
     */
    public static ObservableList<Division> getDivisionsPerCountry(Country country) {
        ObservableList<Division> divisionList = FXCollections.observableArrayList();

        try{
            String query = "SELECT Division_ID, Division, Country_ID FROM first_level_divisions";

            PreparedStatement ps = getConnection().prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                int divisionID = rs.getInt("Division_ID");
                String division = rs.getString("Division");
                int countryID = rs.getInt("Country_ID");

                Division divisionItem = new Division(divisionID, division, countryID);
                if(country.getCountryID() == countryID) {
                    divisionList.add(divisionItem);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return divisionList;
    }
}
