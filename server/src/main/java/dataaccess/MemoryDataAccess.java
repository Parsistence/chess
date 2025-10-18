package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {
    private final HashMap<String, UserData> users = new HashMap<>();

    /**
     * Insert data for a new user into the data store.
     *
     * @param userData The user data to insert.
     * @throws DataAccessException If there is already user data in the data store with the given username.
     */
    @Override
    public void insertUser(UserData userData) throws DataAccessException {
        String username = userData.username();
        var result = users.put(username, userData);

        if (result != null) {
            throw new DataAccessException(
                    "Tried to register a new user with username " + username + ", but there is already a user registered with that name"
            );
        }
    }

    /**
     * Get data for an existing user from the data store.
     *
     * @param username The username of the user whose data is to be retrieved.
     * @return A UserData object corresponding to the given username.
     * @throws DataAccessException If the user data does not exist in the data store.
     */
    @Override
    public UserData getUser(String username) throws DataAccessException {
        UserData userData = users.get(username);

        if (userData == null) {
            throw new DataAccessException(
                    "Tried to get an existing user with username " + username + ", but there is no user registered with that name"
            );
        }

        return userData;
    }
}
