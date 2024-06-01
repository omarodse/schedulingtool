package model;

/**
 * Represents a country entity in the system. This class is used to model the country details,
 * providing methods to manage and access country information such as country ID and name.
 */
public class Country {
    private int countryID;       // Unique identifier for the country
    private String countryName;  // Name of the country

    /**
     * Constructs a new Country instance with specified details.
     * @param countryID The unique identifier of the country.
     * @param countryName The name of the country.
     */
    public Country(int countryID, String countryName) {
        this.countryID = countryID;
        this.countryName = countryName;
    }

    /**
     * Returns the unique identifier of this country.
     * @return the country ID.
     */
    public int getCountryID() {
        return countryID;
    }

    /**
     * Returns the name of this country.
     * @return the country's name.
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * Returns a string representation of this country, combining the country ID and name.
     * This format is useful for identifying both the numeric ID and the descriptive name of the country,
     * especially in user interface components where a clear representation is beneficial.
     * @return a string representation of the country in the format "[ID] Name".
     */
    @Override
    public String toString() {
        return "[" + countryID + "] " + countryName;
    }
}
