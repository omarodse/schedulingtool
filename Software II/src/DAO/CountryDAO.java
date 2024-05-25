package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Country;
import model.Customer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static utilities.DBConnection.getConnection;

public class CountryDAO {

    public static ObservableList<Country> getAllCountries() {
        ObservableList<Country> countryList = FXCollections.observableArrayList();

        try{
            String query = "SELECT Country_ID, Country FROM countries";

            PreparedStatement ps = getConnection().prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
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
