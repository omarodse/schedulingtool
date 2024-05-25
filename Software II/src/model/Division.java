package model;

public class Division {

    private int divisionID;
    private String division;
    private int countryID;

    public Division(int divisionID, String division, int countryID) {
        this.divisionID = divisionID;
        this.division = division;
        this.countryID = countryID;
    }

    public int getDivisionID() {
        return divisionID;
    }

    public String getDivision() {
        return division;
    }

    public int getCountryID() {
        return countryID;
    }

    @Override
    public String toString() {
        return "[" + divisionID + "] " + division;
    }
}
