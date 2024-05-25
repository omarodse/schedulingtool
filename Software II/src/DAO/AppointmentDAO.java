package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointment;
import model.Contact;
import model.Country;
import model.Division;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


import static utilities.DBConnection.getConnection;

public class AppointmentDAO {

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
            ps.setTimestamp(5, Timestamp.valueOf(Start.toLocalDateTime()));
            ps.setTimestamp(6, Timestamp.valueOf(End.toLocalDateTime()));
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

    public static Appointment getUpcomingAppointmentForUser(ZoneId userTimeZone) throws SQLException {
        return getUpcomingAppointment(userTimeZone);
    }

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
                if (upcomingAppointment != null) {
                    System.out.println("Is not NULL from DB");
                } else {
                    System.out.println("Is NULL from DB");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return upcomingAppointment;
    }

    /**
     * Retrieves the user ID from the database based on the username.
     * @param userName The username for which to fetch the user ID.
     * @return The user ID associated with the given username, or -1 if no user is found or an error occurs.
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

    public static int countAppointmentsByTypeAndMonth(String type, int month) {
        String query = "SELECT COUNT(*) AS total FROM appointments WHERE type = ? AND MONTH(Start) = ?";
        int count = 0;

        try{
            PreparedStatement ps = getConnection().prepareStatement(query);

            ps.setString(1, type);
            ps.setInt(2, month);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

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
}
