package model;

/**
 * Represents a geographical division such as a state or province within a country.
 * This class is used to model the division details, providing methods to manage and access
 * division information such as division ID, division name, and associated country ID.
 */
public class Division {
    private int divisionID;    // Unique identifier for the division
    private String division;   // Name of the division
    private int countryID;     // Identifier for the country to which the division belongs

    /**
     * Constructs a new Division instance with specified details.
     *
     * @param divisionID The unique identifier of the division.
     * @param division The name of the division.
     * @param countryID The identifier of the country to which the division belongs.
     */
    public Division(int divisionID, String division, int countryID) {
        this.divisionID = divisionID;
        this.division = division;
        this.countryID = countryID;
    }

    /**
     * Returns the unique identifier of this division.
     * @return the division ID.
     */
    public int getDivisionID() {
        return divisionID;
    }

    /**
     * Returns the name of this division.
     * @return the division's name.
     */
    public String getDivision() {
        return division;
    }

    /**
     * Returns the identifier for the country to which this division belongs.
     * @return the country ID.
     */
    public int getCountryID() {
        return countryID;
    }

    /**
     * Returns a string representation of this division, typically for display in user interfaces.
     * @return a string representation of the division, including its ID and name.
     */
    @Override
    public String toString() {
        return "[" + divisionID + "] " + division;
    }
}
