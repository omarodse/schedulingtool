package model;

public class Contact {

    private int contactID;
    private String contactName;

    public Contact(int contactID, String contactName) {
        this.contactID = contactID;
        this.contactName = contactName;
    }

    public int getContactID() {
        return contactID;
    }

    public String getContactName() {
        return contactName;
    }

    @Override
    public String toString() {
        return contactName;
    }
}
