package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

public class UserService {
    private final AuthService authService;
    private final DataAccess dataAccess;

    public UserService(AuthService authService, DataAccess dataAccess) {
        this.authService = authService;
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData userData) throws DataAccessException {
        dataAccess.insertUser(userData);
        return authService.createAuth(userData);
    }

    /**
     * Clear all user data from the server.
     */
    public void clearAll() throws DataAccessException {
        dataAccess.clearUsers();
    }

    /**
     * Gets a user in the server given the user's auth token.
     *
     * @param authToken The auth token associated with the user.
     * @return The user data.
     */
    public UserData getUser(String authToken) throws DataAccessException {
        return dataAccess.getUserFromAuth(authToken);
    }
}
