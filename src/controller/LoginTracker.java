package controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Provides utility functions for logging user login attempts to a file.
 * This class handles the creation and updating of a log file named {@code login_activity.txt},
 * which records each login attempt with a timestamp, the username, and the outcome of the attempt.
 */
public class LoginTracker {
    private static final String FILE_PATH = "login_activity.txt"; // Path to the log file

    /**
     * Logs a single login attempt to the log file. The method records the timestamp, username, and
     * the result of the login attempt. It appends this information to {@code login_activity.txt},
     * creating the file if it does not already exist.
     *
     * @param username The username of the account that attempted to log in.
     * @param isSuccess A boolean indicating whether the login attempt was successful.
     */
    public static void logLoginAttempt(String username, boolean isSuccess) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logMessage = String.format("Timestamp: %s, Username: %s, Success: %s%n", timestamp, username, isSuccess);

        try {
            // Append the log message to the file, creating the file if it doesn't exist.
            Files.write(Paths.get(FILE_PATH), logMessage.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Error writing to login activity log: " + e.getMessage());
        }
    }
}

