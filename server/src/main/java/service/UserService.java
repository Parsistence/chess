package service;

import dataaccess.DataAccess;
import dataaccess.EntryAlreadyExistsException;
import dataaccess.EntryNotFoundException;
import model.AuthData;
import model.UserData;
import server.LoginRequest;

import java.util.Objects;

public class UserService {
    private final AuthService authService;
    private final DataAccess dataAccess;

    public UserService(AuthService authService, DataAccess dataAccess) {
        this.authService = authService;
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData userData) throws EntryAlreadyExistsException {
        dataAccess.insertUser(userData);
        return new AuthData(userData.username(), authService.generateToken());
    }

    /**
     * Attempt to log in an existing user and get an auth token for the user.
     *
     * @param req The LoginRequest object.
     * @return An AuthData with the user's auth token.
     */
    public AuthData login(LoginRequest req) throws EntryNotFoundException, EntryAlreadyExistsException {
        UserData user = dataAccess.getUser(req.username());

        if (!req.password().equals(user.password())) {
            throw new EntryNotFoundException("Password does not match");
        }

        return authService.createAuth(user);
    }

    /**
     * Clear all user data from the server.
     */
    public void clearAll() {
        dataAccess.clearUsers();
    }
}
