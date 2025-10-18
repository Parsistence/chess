package service;

import dataaccess.DataAccess;
import datamodel.AuthData;
import datamodel.UserData;

public class UserService {
    private final AuthService authService;
    private final DataAccess dataAccess;

    public UserService(AuthService authService, DataAccess dataAccess) {
        this.authService = authService;
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData userData) {
        dataAccess.saveUser(userData);
        return new AuthData(userData.username(), authService.generateToken());
    }
}
