package model;

import java.time.LocalDateTime;

public class Appointment {

    private int appointmentID;
    private String title;
    private String description;
    private String location;
    private String type;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int customerID;
    private int userID;
    private int contactID;

    public Appointment(int appointmentID, String title, String description, String location, String type, LocalDateTime startDate, LocalDateTime endDate, int customerID, int userID, int contactID) {
        this.appointmentID = appointmentID;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.customerID = customerID;
        this.userID = userID;
        this.contactID = contactID;
    }

    public Appointment(int appointmentID, String title, String description, String type, LocalDateTime startDate, LocalDateTime endDate, int customerID) {
        this.appointmentID = appointmentID;
        this.title = title;
        this.description = description;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.customerID = customerID;
    }

    public Appointment(int appointmentID, LocalDateTime startDate) {
        this.appointmentID = appointmentID;
        this.startDate = startDate;
    }

    public int getAppointmentID() {
        return appointmentID;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getType() {
        return type;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }
    public int getCustomerID() {
        return customerID;
    }
    public int getUserID() {
        return userID;
    }
    public int getContactID() {
        return contactID;
    }
}
