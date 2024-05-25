package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Country;
import model.Division;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static utilities.DBConnection.getConnection;

public class DivisionDAO {

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
