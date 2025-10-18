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
        return new AuthData(userData.username(), authService.generateToken());
    }
}
