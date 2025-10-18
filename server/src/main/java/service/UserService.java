package service;

import dataaccess.DataAccess;
import datamodel.RegistrationResult;
import datamodel.UserData;

public class UserService {
    private final AuthService authService;
    private final DataAccess dataAccess;

    public UserService(AuthService authService, DataAccess dataAccess) {
        this.authService = authService;
        this.dataAccess = dataAccess;
    }

    public RegistrationResult register(UserData userData) {
        dataAccess.saveUser(userData);
        return new RegistrationResult(userData.username(), authService.generateToken());
    }
}
