package model;

/**
 * Represents a customer entity in the system. This class is used to model the customer details,
 * providing methods to manage and access customer information such as customer ID, name, address,
 * postal code, phone number, and division.
 */
public class Customer {
    private int customerID;       // Unique identifier for the customer
    private String customerName;  // Name of the customer
    private String address;       // Address of the customer
    private String postalCode;    // Postal code of the customer's address
    private String phone;         // Phone number of the customer
    private String division;      // Division or region of the customer

    /**
     * Constructs a new Customer instance with specified details.
     * @param customerID The unique identifier of the customer.
     * @param customerName The name of the customer.
     * @param address The address of the customer.
     * @param postalCode The postal code of the customer's address.
     * @param phone The phone number of the customer.
     * @param division The division or region of the customer.
     */
    public Customer(int customerID, String customerName, String address, String postalCode, String phone, String division) {
        this.customerID = customerID;
        this.customerName = customerName;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.division = division;
    }

    /**
     * Returns the unique identifier of this customer.
     * @return the customer ID.
     */
    public int getCustomerID() {
        return customerID;
    }

    /**
     * Returns the name of this customer.
     * @return the customer's name.
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Returns the address of this customer.
     * @return the customer's address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Returns the postal code of this customer's address.
     * @return the postal code.
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Returns the phone number of this customer.
     * @return the customer's phone number.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Returns the division or region of this customer.
     * @return the division.
     */
    public String getDivision() {
        return division;
    }
}
