package service;

import dataaccess.DataAccess;
import datamodel.RegistrationResult;
import datamodel.User;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public RegistrationResult register(User user) {
        // TODO: Implement this
        dataAccess.saveUser(user);
        return new RegistrationResult(user.username(), "foo");
    }
}
