package dataaccess;

import model.UserData;

public interface DataAccess {
    /**
     * Clears all user data in the database.
     */
    void clearUsers();

    /**
     * Clears all auth data in the database.
     */
    void clearAuthData();

    /**
     * Clears all game data in the database.
     */
    void clearGameData();

    /**
     * Insert data for a new user into the data store.
     *
     * @param userData The user data to insert.
     * @throws EntryAlreadyExistsException If the user data was unable to be inserted into the data store.
     */
    void insertUser(UserData userData) throws EntryAlreadyExistsException;

    /**
     * Get data for an existing user from the data store.
     *
     * @param username The username of the user whose data is to be retrieved.
     * @return A UserData object corresponding to the given username.
     * @throws EntryNotFoundException If the user data was unable to be retrieved from the data store.
     */
    UserData getUser(String username) throws EntryNotFoundException;
}
