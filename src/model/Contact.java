package model;

/**
 * Represents a contact entity in the system. This class is used to model the contact details,
 * providing methods to manage and access contact information such as contact ID and name.
 */
public class Contact {

    private int contactID;       // Unique identifier for the contact
    private String contactName;  // Name of the contact

    /**
     * Constructs a new Contact instance with specified details.
     * @param contactID The unique identifier of the contact.
     * @param contactName The name of the contact.
     */
    public Contact(int contactID, String contactName) {
        this.contactID = contactID;
        this.contactName = contactName;
    }

    /**
     * Returns the unique identifier of this contact.
     * @return the contact ID.
     */
    public int getContactID() {
        return contactID;
    }

    /**
     * Returns a string representation of this contact, which is the contact's name.
     * This is particularly useful when displaying the contact in UI components.
     * @return the contact's name.
     */
    @Override
    public String toString() {
        return contactName;
    }
}
