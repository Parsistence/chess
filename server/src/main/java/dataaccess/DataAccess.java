package dataaccess;

import model.UserData;

public interface DataAccess {
    /**
     * Insert data for a new user into the data store.
     *
     * @param userData The user data to insert.
     * @throws DataAccessException If the user data was unable to be inserted into the data store.
     */
    void insertUser(UserData userData) throws DataAccessException;

    /**
     * Get data for an existing user from the data store.
     *
     * @param username The username of the user whose data is to be retrieved.
     * @return A UserData object corresponding to the given username.
     * @throws DataAccessException If the user data was unable to be retrieved from the data store.
     */
    UserData getUser(String username) throws DataAccessException;
}
