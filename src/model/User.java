package model;

/**
 * Represents a user within the system. This class is used to model user details,
 * providing methods to manage and access user information such as user ID, username,
 * and password. It is essential for authentication and user management processes.
 */
public class User {
    private int userID;     // Unique identifier for the user
    private String userName; // Username of the user
    private int password;    // User's password, typically stored as a hashed value

    /**
     * Constructs a new User instance with specified details. Note that the password
     * should ideally be a hashed value to ensure security.
     *
     * @param userID The unique identifier of the user.
     * @param userName The username of the user.
     * @param password The hashed password of the user.
     */
    public User(int userID, String userName, int password) {
        this.userID = userID;
        this.userName = userName;
        this.password = password;
    }

    /**
     * Returns the unique identifier of this user.
     *
     * @return the user ID.
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Returns the username of this user.
     *
     * @return the username.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Returns the password of this user. This should be treated with a high
     * level of security and never exposed publicly.
     *
     * @return the password.
     */
    public int getPassword() {
        return password;
    }
}
