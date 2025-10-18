package service;

import dataaccess.DataAccess;
import datamodel.RegistrationResult;
import datamodel.UserData;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public RegistrationResult register(UserData userData) {
        // TODO: Implement this
        dataAccess.saveUser(userData);
        return new RegistrationResult(userData.username(), "foo");
    }
}
