package utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages the connection to the database.
 * <p>
 * This class provides methods for connecting, return the connection to the database, and
 * disconnect from the DB.
 */
public abstract class DBConnection {
    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "client_schedule";
    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone = SERVER"; // LOCAL
    private static final String driver = "com.mysql.cj.jdbc.Driver"; // Driver reference
    private static final String userName = "sqlUser"; // Username
    private static String password = "Passw0rd!"; // Password
    public static Connection connection;  // Connection Interface

    /**
     * Establish the connection to the database.
     *<p>
     * @return The current connection.
     */
    public static Connection openConnection()
    {
        try {
            Class.forName(driver); // Locate Driver
            connection = DriverManager.getConnection(jdbcUrl, userName, password); // Reference Connection object
            System.out.println("Connection successful!");
        }
        catch(SQLException | ClassNotFoundException e)
        {
            //System.out.println("Error:" + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Gets the current connection.
     *<p>
     * @return The current connection.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed() || !connection.isValid(5)) {
            connection = openConnection();
        }
        return connection;
    }

    /**
     * Closes the connection to the database.
     *
     */
    public static void closeConnection() {
        try {
            connection.close();
            System.out.println("Connection closed!");
        }
        catch(Exception e)
        {
            //not a problem
        }
    }
}
