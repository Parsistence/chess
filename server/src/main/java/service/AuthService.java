package service;

import dataaccess.DataAccess;
import dataaccess.EntryAlreadyExistsException;
import dataaccess.EntryNotFoundException;
import model.AuthData;
import model.UserData;
import server.LoginRequest;

import java.util.UUID;

public class AuthService {
    private final DataAccess dataAccess;

    public AuthService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Generate an auth token for a user and add it to the server.
     *
     * @param userData The user data to create the auth token for.
     * @return The auth data that was added to the server.
     */
    public AuthData createAuth(UserData userData) throws EntryAlreadyExistsException {
        AuthData authData = new AuthData(
                userData.username(),
                generateToken()
        );

        dataAccess.insertAuthData(authData);

        return authData;
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

        return createAuth(user);
    }

    /**
     * Clear all auth data from the server.
     */
    public void clearAll() {
        dataAccess.clearAuthData();
    }
}
