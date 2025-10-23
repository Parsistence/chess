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
     * Clear all user data from the server.
     */
    public void clearAll() {
        dataAccess.clearUsers();
    }
}
