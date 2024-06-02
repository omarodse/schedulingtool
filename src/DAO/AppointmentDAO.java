package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointment;
import model.Contact;

import java.sql.Timestamp;
import java.time.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static utilities.DBConnection.getConnection;

/**
 * Handles all database operations related to appointments. This class is responsible for creating, updating,
 * deleting, and fetching appointment data from the database. It provides methods to:
 * - Retrieve all appointments or specific subsets based on various criteria such as customer or contact.
 * - Add new appointments to the database with detailed information including start and end times, descriptions, and associated user and customer details.
 * - Update existing appointment records when changes are made.
 * - Delete appointments from the database.
 * - Count appointments based on type and time criteria, useful for reporting and analytics.
 * - Fetch unique appointment types for dynamic filtering or categorization within the application.
 * Each method ensures data integrity and handles SQL exceptions to maintain robustness of the application's data access layer.
 */
public class AppointmentDAO {

    /**
     * Retrieves all appointments from the database, joining with customer and contact tables to enrich the appointment data.
     * Orders the appointments by creation date.
     * @return an ObservableList of all appointments in the database.
     * @throws SQLException if there is a problem executing the query.
     */
    public static ObservableList<Appointment> getAllAppointments() {
        ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

        try{
            String query = "SELECT a.Appointment_ID, a.Title, a.Description, a.Location, a.Type, a.Start, a.End, a.Customer_ID, a.User_ID, ct.Contact_ID " +
                           "FROM appointments a " +
                           "JOIN customers c ON a.Customer_ID = c.Customer_ID " +
                           "JOIN contacts ct ON a.Contact_ID = ct.Contact_ID " +
                           "ORDER BY a.Create_Date ASC";

            PreparedStatement ps = getConnection().prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                int appointmentID = rs.getInt("Appointment_ID");
                String title = rs.getString("Title");
                String description = rs.getString("Description");
                String location = rs.getString("Location");
                String type = rs.getString("Type");
                LocalDateTime startDate = rs.getTimestamp("Start") != null ? rs.getTimestamp("Start").toLocalDateTime() : null;
                System.out.println("From getAllAppointments() = " + startDate);
                System.out.println("Timestamp = " + rs.getTimestamp("Start"));
                System.out.println("From getAllAppointments() = " + title);
                LocalDateTime endDate = rs.getTimestamp("End") != null ? rs.getTimestamp("End").toLocalDateTime() : null;
                int customerID = rs.getInt("Customer_ID");
                int userID = rs.getInt("User_ID");
                int contactID = rs.getInt("Contact_ID");

                Appointment appointment = new Appointment(appointmentID, title, description, location, type, startDate, endDate, customerID, userID, contactID);
                appointmentList.add(appointment);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointmentList;
    }

    /**
     * Fetches all appointments for a specific customer from the database.
     * Filters appointments based on the provided customer ID.
     * @param Customer_ID The ID of the customer for whom appointments are to be retrieved.
     * @return an ObservableList of Appointment objects specific to the given customer.
     * @throws SQLException if there is a problem executing the query.
     */
    public static ObservableList<Appointment> getAllAppointmentsForCustomer(int Customer_ID) {
        ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

        try{
            String query = "SELECT a.Appointment_ID, a.Title, a.Description, a.Location, a.Type, a.Start, a.End, a.Customer_ID, a.User_ID, ct.Contact_ID " +
                    "FROM appointments a " +
                    "JOIN customers c ON a.Customer_ID = c.Customer_ID " +
                    "JOIN contacts ct ON a.Contact_ID = ct.Contact_ID " +
                    "WHERE a.Customer_ID = ? " +
                    "ORDER BY a.Create_Date ASC";

            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setInt(1, Customer_ID);

            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                int appointmentID = rs.getInt("Appointment_ID");
                String title = rs.getString("Title");
                String description = rs.getString("Description");
                String location = rs.getString("Location");
                String type = rs.getString("Type");
                LocalDateTime startDate = rs.getTimestamp("Start") != null ? rs.getTimestamp("Start").toLocalDateTime() : null;
                LocalDateTime endDate = rs.getTimestamp("End") != null ? rs.getTimestamp("End").toLocalDateTime() : null;
                int customerID = rs.getInt("Customer_ID");
                int userID = rs.getInt("User_ID");
                int contactID = rs.getInt("Contact_ID");

                Appointment appointment = new Appointment(appointmentID, title, description, location, type, startDate, endDate, customerID, userID, contactID);
                appointmentList.add(appointment);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointmentList;
    }

    /**
     * Inserts a new appointment into the database with detailed information including title, description, location, type, and timing.
     * @param Title The title of the appointment.
     * @param Description The description of the appointment.
     * @param Location The location of the appointment.
     * @param Type The type of the appointment.
     * @param Start The start time of the appointment, in UTC.
     * @param End The end time of the appointment, in UTC.
     * @param Customer_ID The customer ID related to the appointment.
     * @param User_ID The user ID related to the appointment.
     * @param Contact_ID The contact ID related to the appointment.
     * @throws SQLException if there is an error during the query execution.
     */
    public static void createAppointment(String Title, String Description, String Location, String Type,
                                         ZonedDateTime Start, ZonedDateTime End, int Customer_ID, int User_ID, int Contact_ID) {

        PreparedStatement ps = null;

        try{
            String query = "INSERT INTO appointments (Title, Description, Location, Type, Start, End, Customer_ID, User_ID, Contact_ID, Create_Date, Created_By, Last_Update, Last_Updated_By) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), 'default_user', NOW(), 'default_user')";

            ps = getConnection().prepareStatement(query);

            ps.setString(1, Title);
            ps.setString(2, Description);
            ps.setString(3, Location);
            ps.setString(4, Type);
            ps.setTimestamp(5, Timestamp.valueOf(Start.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()));
            ps.setTimestamp(6, Timestamp.valueOf(End.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()));
            System.out.println("From createAppointment() = " + Timestamp.valueOf(Start.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()));
            ps.setInt(7, Customer_ID);
            ps.setInt(8, User_ID);
            ps.setInt(9, Contact_ID);

            int result = ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close(); // Close PreparedStatement
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Updates an existing appointment in the database with new details.
     * @param Title The new title of the appointment.
     * @param Description The new description of the appointment.
     * @param Location The new location of the appointment.
     * @param Type The new type of the appointment.
     * @param Start The new start time of the appointment, adjusted to UTC.
     * @param End The new end time of the appointment, adjusted to UTC.
     * @param Customer_ID The customer ID related to the appointment.
     * @param User_ID The user ID related to the appointment.
     * @param Contact_ID The contact ID related to the appointment.
     * @param Appointment_ID The ID of the appointment to update.
     * @throws SQLException if there is an error during the update process.
     */
    public static void editAppointment(String Title, String Description, String Location, String Type,
                                       ZonedDateTime Start, ZonedDateTime End, int Customer_ID, int User_ID, int Contact_ID, int Appointment_ID) {

        try{
            String query = "UPDATE appointments set Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ?, Last_Update = NOW() " +
                           "WHERE Appointment_ID = ?";

            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setString(1, Title);
            ps.setString(2, Description);
            ps.setString(3, Location);
            ps.setString(4, Type);
            ps.setTimestamp(5, Timestamp.valueOf(Start.toLocalDateTime()));
            ps.setTimestamp(6, Timestamp.valueOf(End.toLocalDateTime()));
            ps.setInt(7, Customer_ID);
            ps.setInt(8, User_ID);
            ps.setInt(9, Contact_ID);
            ps.setInt(10, Appointment_ID);

            ps.execute();

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes an appointment from the database based on the provided appointment ID.
     * @param Appointment_ID The ID of the appointment to be deleted.
     * @throws SQLException if there is an error executing the deletion query.
     */
    public static void deleteAppointmentFromDB(int Appointment_ID) {

        try{
            String query = "DELETE from appointments " +
                    "WHERE Appointment_ID = ?";

            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setInt(1, Appointment_ID);

            ps.execute();

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fetches appointments from the database within a specified date range.
     * @param start The start date to filter the appointments.
     * @param end The end date to filter the appointments.
     * @return an ObservableList of appointments within the specified date range.
     * @throws SQLException if there is a problem executing the query.
     */
    public static ObservableList<Appointment> fetchAppointments(LocalDate start, LocalDate end) {
        ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

        try{
            String query = "SELECT a.Appointment_ID, a.Title, a.Description, a.Location, a.Type, a.Start, a.End, a.Customer_ID, a.User_ID, ct.Contact_ID " +
                    "FROM appointments a " +
                    "JOIN customers c ON a.Customer_ID = c.Customer_ID " +
                    "JOIN contacts ct ON a.Contact_ID = ct.Contact_ID " +
                    "WHERE a.Start >= ? AND a.Start <= ? " +
                    "ORDER BY a.Create_Date ASC";

            PreparedStatement ps = getConnection().prepareStatement(query);

            ps.setDate(1, java.sql.Date.valueOf(start));
            ps.setDate(2, java.sql.Date.valueOf(end));

            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                int appointmentID = rs.getInt("Appointment_ID");
                String title = rs.getString("Title");
                String description = rs.getString("Description");
                String location = rs.getString("Location");
                String type = rs.getString("Type");
                LocalDateTime startDate = rs.getTimestamp("Start") != null ? rs.getTimestamp("Start").toLocalDateTime() : null;
                LocalDateTime endDate = rs.getTimestamp("End") != null ? rs.getTimestamp("End").toLocalDateTime() : null;
                int customerID = rs.getInt("Customer_ID");
                int userID = rs.getInt("User_ID");
                int contactID = rs.getInt("Contact_ID");


                Appointment appointment = new Appointment(appointmentID, title, description, location, type, startDate, endDate, customerID, userID, contactID);
                appointmentList.add(appointment);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointmentList;
    }

    /**
     * Fetches the nearest upcoming appointment for a user based on the current time adjusted to UTC and considering a 15-minute window.
     * The user's time zone is used to adjust the appointment time to the local time zone for display.
     * @param userTimeZone The time zone to adjust the appointment times to.
     * @return The next upcoming appointment if it exists within 15 minutes of the current UTC time; otherwise, null.
     * @throws SQLException If there is a database access error.
     */
    public static Appointment getUpcomingAppointmentForUser(ZoneId userTimeZone) throws SQLException {
        return getUpcomingAppointment(userTimeZone);
    }

    /**
     * Queries the database for the next upcoming appointment within a 15-minute window from now, using UTC times for comparison.
     * Converts fetched UTC appointment times to the user's local time zone for accurate display and evaluation.
     * @param userTimeZone The user's local time zone for correct time display.
     * @return An upcoming appointment if available within the next 15 minutes, otherwise null.
     * @throws SQLException If there is a database access error or the query fails to execute.
     */
    public static Appointment getUpcomingAppointment(ZoneId userTimeZone) throws SQLException {
        // Convert current time to UTC
        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
        LocalDateTime nowPlus15Minutes = now.plusMinutes(15);

        String query = "SELECT Appointment_ID, Start FROM appointments WHERE Start BETWEEN ? AND ? ORDER BY Start ASC LIMIT 1;";
        Appointment upcomingAppointment = null;
        try{
            PreparedStatement ps = getConnection().prepareStatement(query);
            // Set parameters for UTC times
            ps.setTimestamp(1, Timestamp.valueOf(now));
            ps.setTimestamp(2, Timestamp.valueOf(nowPlus15Minutes));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int appointmentId = rs.getInt("Appointment_ID");
                Timestamp startTimestamp = rs.getTimestamp("Start");
                LocalDateTime startUTC = startTimestamp.toLocalDateTime();
                ZonedDateTime startUserTime = startUTC.atZone(ZoneId.of("UTC")).withZoneSameInstant(userTimeZone);

                upcomingAppointment = new Appointment(appointmentId, startUserTime.toLocalDateTime());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return upcomingAppointment;
    }

    /**
     * Retrieves the user ID associated with a given username from the database.
     * This method is crucial for operations requiring user identification beyond just username checks, such as logging and user-specific data retrieval.
     * @param userName The username whose user ID is being queried.
     * @return The user ID corresponding to the provided username or -1 if the user does not exist or an error occurs.
     * @throws SQLException If there is an error during the database access.
     */

    public static int getUserIdFromUserName(String userName) throws SQLException {
        String query = "SELECT User_ID FROM users WHERE User_Name = ?";
        int userId = -1;  // Default to -1 to indicate no user found or error

        try{
            PreparedStatement ps = getConnection().prepareStatement(query);

            ps.setString(1, userName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    userId = rs.getInt("User_ID");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user ID from database for username: " + userName);
            e.printStackTrace();
            throw e;
        }

        return userId;
    }

    /**
     * Retrieves all appointments associated with a specific contact from the database.
     * This method is used to filter appointments by contact, which is useful for displaying user-specific appointment data.
     * @param contact The contact whose appointments are to be fetched.
     * @return An ObservableList containing all appointments linked to the specified contact.
     * @throws SQLException If there is a database access error.
     */

    public static ObservableList<Appointment> getAppointmentsPerContact(Contact contact) {
        ObservableList<Appointment> appointmentsPerContact = FXCollections.observableArrayList();

        try{
            String query = "SELECT Appointment_ID, Title, Type, Description, Start, End, Customer_ID " +
                           "FROM appointments " +
                           "WHERE Contact_ID = ?";

            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setInt(1, contact.getContactID());

            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                int appointmentID = rs.getInt("Appointment_ID");
                String title = rs.getString("Title");
                String description = rs.getString("Description");
                String type = rs.getString("Type");
                LocalDateTime start = rs.getTimestamp("Start") != null ? rs.getTimestamp("Start").toLocalDateTime() : null;
                LocalDateTime end = rs.getTimestamp("End") != null ? rs.getTimestamp("End").toLocalDateTime() : null;
                int customerID = rs.getInt("Customer_ID");

                Appointment appointment = new Appointment(appointmentID, title, description, type, start, end, customerID);
                appointmentsPerContact.add(appointment);
                }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointmentsPerContact;
    }

    /**
     * Counts the number of appointments of a specified type occurring in a specific month.
     * This method is useful for generating statistics and reports on appointment data.
     * @param month The month for which to count appointments.
     * @return The number of appointments matching the specified type and month.
     * @throws SQLException If there is an error executing the count query.
     */
    public static ObservableList<Appointment> countAppointmentsByTypeForMonth(int month) {
        ObservableList<Appointment> summaryList = FXCollections.observableArrayList();
        String query = "SELECT type, COUNT(*) AS total " +
                       "FROM appointments " +
                       "WHERE MONTH(Start) = ? " +
                       "GROUP BY type";

        try{
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setInt(1, month);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String type = rs.getString("type");
                    int total = rs.getInt("total");
                    System.out.println(type);
                    System.out.println(total);
                    summaryList.add(new Appointment(type, total));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return summaryList;
    }
    /**
     * Retrieves a distinct list of all appointment types from the database.
     * This method is typically used to populate type selection controls or for filtering purposes.
     * @return An ObservableList containing the distinct types of appointments.
     * @throws SQLException If there is an error during database access.
     */
    public static ObservableList<String> fetchAppointmentTypes() {
        ObservableList<String> types = FXCollections.observableArrayList();
        String query = "SELECT DISTINCT type FROM appointments ORDER BY type";

        try{
            PreparedStatement ps = getConnection().prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                types.add(rs.getString("type"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return types;
    }

    /**
     * Counts all appointments scheduled for the next day from the current date.
     * This method can be used to prepare or alert users about upcoming appointments.
     * @return The total number of appointments scheduled for the next day.
     * @throws SQLException If there is an error executing the count query.
     */
    public static int getCountOfAppointmentsForNextDay() {
        String query = "SELECT COUNT(*) AS total FROM appointments WHERE DATE(Start) = CURDATE() + INTERVAL 1 DAY";
        int count = 0;

        try{
            PreparedStatement ps = getConnection().prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Deletes all appointments associated with a given customer from the database.
     * @param customerID The ID of the customer whose appointments are to be deleted.
     * @throws SQLException If a database access error occurs or the delete operation fails.
     */
    public static void deleteAllAppointmentsForCustomer(int customerID) throws SQLException {
        String query = "DELETE FROM appointments WHERE Customer_ID = ?;";

        try{
            PreparedStatement ps = getConnection().prepareStatement(query);
            ps.setInt(1, customerID);
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Deleted " + affectedRows + " appointments for customer ID " + customerID);
            } else {
                System.out.println("No appointments found for customer ID " + customerID);
            }
        } catch (SQLException e) {
            System.err.println("Error deleting appointments for customer ID " + customerID);
            e.printStackTrace();
            throw e;
        }
    }
}
