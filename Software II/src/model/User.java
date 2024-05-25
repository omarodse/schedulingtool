package model;

public class User {
    private int userID;
    private String userName;
    private int password;

    public User(int userID, String userName, int password) {
        this.userID = userID;
        this.userName = userName;
        this.password = password;
    }

    public int getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public int getPassword() {
        return password;
    }
}
