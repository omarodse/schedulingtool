package model;

import java.time.LocalDateTime;

/**
 * Represents an appointment in the application. This class models the necessary attributes
 * and methods required to manage an appointment, including its unique identifier, associated
 * user, customer, and contact details, as well as timing and descriptive information.
 */
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

    /**
     * Constructs a fully specified appointment with all details.
     *
     * @param appointmentID Unique identifier for the appointment.
     * @param title Title or subject of the appointment.
     * @param description Detailed description of the appointment.
     * @param location Location where the appointment takes place.
     * @param type Type of the appointment, categorizing its purpose.
     * @param startDate Start date and time of the appointment.
     * @param endDate End date and time of the appointment.
     * @param customerID Identifier of the customer involved in the appointment.
     * @param userID Identifier of the user who manages or oversees the appointment.
     * @param contactID Identifier of the contact associated with the appointment.
     */
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

    /**
     * Constructs an appointment with reduced details, typically used when only key aspects
     * of the appointment are required.
     *
     * @param appointmentID Unique identifier for the appointment.
     * @param title Title or subject of the appointment.
     * @param description Detailed description of the appointment.
     * @param type Type of the appointment.
     * @param startDate Start date and time of the appointment.
     * @param endDate End date and time of the appointment.
     * @param customerID Identifier of the customer involved in the appointment.
     */
    public Appointment(int appointmentID, String title, String description, String type, LocalDateTime startDate, LocalDateTime endDate, int customerID) {
        this.appointmentID = appointmentID;
        this.title = title;
        this.description = description;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.customerID = customerID;
    }

    /**
     * Constructs an appointment focusing solely on its timing, typically used in scenarios
     * where only the timing is relevant.
     *
     * @param appointmentID Unique identifier for the appointment.
     * @param startDate Start date and time of the appointment.
     */
    public Appointment(int appointmentID, LocalDateTime startDate) {
        this.appointmentID = appointmentID;
        this.startDate = startDate;
    }

    // Getters and setters
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
