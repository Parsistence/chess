package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashMap;
import java.util.Objects;

public class MemoryDataAccess implements DataAccess {
    private final HashMap<String, UserData> users = new HashMap<>();
    private final HashMap<String, AuthData> authDataMap = new HashMap<>();
    private final HashMap<String, GameData> games = new HashMap<>();

    /**
     * Clears all user data in the database, resulting in a clean wipe.
     */
    @Override
    public void clearUsers() {
        users.clear();
    }

    /**
     * Clears all auth data in the database.
     */
    @Override
    public void clearAuthData() {
        authDataMap.clear();
    }

    /**
     * Clears all game data in the database.
     */
    @Override
    public void clearGameData() {
        games.clear();
    }

    /**
     * Insert data for a new user into the data store.
     *
     * @param userData The user data to insert.
     * @throws EntryAlreadyExistsException If there is already user data in the data store with the given username.
     */
    @Override
    public void insertUser(UserData userData) throws EntryAlreadyExistsException {
        String username = userData.username();
        var result = users.put(username, userData);

        if (result != null) {
            throw new EntryAlreadyExistsException(
                    "Tried to register a new user with username " + username + ", but there is already a user registered with that name"
            );
        }
    }

    /**
     * Get data for an existing user from the data store.
     *
     * @param username The username of the user whose data is to be retrieved.
     * @return A UserData object corresponding to the given username.
     * @throws EntryNotFoundException If the user data does not exist in the data store.
     */
    @Override
    public UserData getUser(String username) throws EntryNotFoundException {
        UserData userData = users.get(username);

        if (userData == null) {
            throw new EntryNotFoundException(
                    "Tried to get an existing user with username " + username + ", but there is no user registered with that name"
            );
        }

        return userData;
    }

    /**
     * Insert new auth data into the database
     *
     * @param authData The auth data to insert
     * @throws EntryAlreadyExistsException If auth data already exists in database
     */
    @Override
    public void insertAuthData(AuthData authData) throws EntryAlreadyExistsException {
        String authToken = authData.authToken();
        var result = authDataMap.put(authToken, authData);

        if (result != null) {
            throw new EntryAlreadyExistsException(
                    "Tried to save a new auth token for user " + authData.username() + ", but the auth token already exists in the database"
            );
        }
    }

    /**
     * Get auth data from the database.
     *
     * @param authToken The auth token corresponding to the auth data.
     * @return The auth data.
     */
    @Override
    public AuthData getAuthData(String authToken) throws EntryNotFoundException {
        AuthData authData = authDataMap.get(authToken);

        if (authData == null) {
            throw new EntryNotFoundException("Tried to get auth data for an auth token that does not exist");
        }

        return authData;
    }

    /**
     * Remove auth data from the database.
     *
     * @param authToken the auth token associated with the auth data.
     */
    @Override
    public void removeAuth(String authToken) {
        authDataMap.remove(authToken);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MemoryDataAccess that = (MemoryDataAccess) o;
        return Objects.equals(users, that.users);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(users);
    }

    @Override
    public String toString() {
        return "MemoryDataAccess{" +
                "users=" + users +
                '}';
    }
}
