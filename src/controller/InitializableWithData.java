package controller;

/**
 * An interface defining a method for initializing controllers with specific data.
 * This interface is intended to be implemented by controllers that require initialization with data
 * that is not passed through the constructor but needs to be set immediately after the controller's creation.
 * This method allows for the flexible initialization of controllers within JavaFX applications, particularly useful
 * in scenarios where controllers must be dynamically provided with data retrieved from databases or other sources
 * immediately after their instantiation and before being displayed.
 */
public interface InitializableWithData {
    /**
     * Initializes the controller with the specified data. This method should set up the controller's state
     * or UI components based on the data object provided.
     * @param data The data object used to initialize the controller. This object can be any type,
     *             depending on the specific needs of the controller implementation.
     */
    void initializeData(Object data);
}

