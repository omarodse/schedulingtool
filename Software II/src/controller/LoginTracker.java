package controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class LoginTracker {
    private static final String FILE_PATH = "login_activity.txt";

    public static void logLoginAttempt(String username, boolean isSuccess) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logMessage = String.format("Timestamp: %s, Username: %s, Success: %s%n", timestamp, username, isSuccess);

        try {
            Files.write(Paths.get(FILE_PATH), logMessage.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Error writing to login activity log: " + e.getMessage());
        }
    }
}
