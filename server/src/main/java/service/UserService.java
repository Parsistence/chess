package service;

import dataaccess.DataAccess;
import dataaccess.EntryAlreadyExistsException;
import dataaccess.EntryNotFoundException;
import model.AuthData;
import model.UserData;

public class UserService {
    private final AuthService authService;
    private final DataAccess dataAccess;

    public UserService(AuthService authService, DataAccess dataAccess) {
        this.authService = authService;
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData userData) throws EntryAlreadyExistsException {
        dataAccess.insertUser(userData);
        return authService.createAuth(userData);
    }

    /**
     * Clear all user data from the server.
     */
    public void clearAll() {
        dataAccess.clearUsers();
    }

    /**
     * Gets a user in the server given the user's auth token.
     *
     * @param authToken The auth token associated with the user.
     * @return The user data.
     */
    public UserData getUser(String authToken) throws EntryNotFoundException {
        return dataAccess.getUserFromAuth(authToken);
    }
}
