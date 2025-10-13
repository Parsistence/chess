package service;

import datamodel.RegistrationResult;
import datamodel.User;

public class UserService {
    public RegistrationResult register(User user) {
        // TODO: Implement this
        return new RegistrationResult(user.username(), "foo");
    }
}
