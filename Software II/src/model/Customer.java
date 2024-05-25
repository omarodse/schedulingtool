package model;

public class Customer {

    private int customerID;
    private String customerName;
    private String address;
    private String postalCode;
    private String phone;
    private String division;

    public Customer(int customerID, String customerName, String address, String postalCode, String phone, String division) {
        this.customerID = customerID;
        this.customerName = customerName;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.division = division;
    }

    public int getCustomerID() {
        return customerID;
    }
    public String getCustomerName() {
        return customerName;
    }

    public String getAddress() {
        return address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public String getDivision() {
        return division;
    }
}
