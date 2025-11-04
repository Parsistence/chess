package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
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
    public AuthData createAuth(UserData userData) throws DataAccessException {
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
    public AuthData login(LoginRequest req) throws DataAccessException {
        UserData user = dataAccess.getUser(req.username());

        if (!dataAccess.verifyPassword(user.username(), req.password())) {
            throw new EntryNotFoundException("Password does not match");
        }

        return createAuth(user);
    }

    /**
     * Given an auth token, logs out the user associated with that auth token
     *
     * @param authToken The auth token associated with the user.
     */
    public void logout(String authToken) throws DataAccessException {
        try {
            dataAccess.getAuthData(authToken);
        } catch (EntryNotFoundException e) {
            throw new EntryNotFoundException(e.toString());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        dataAccess.removeAuth(authToken);
    }

    /**
     * Clear all auth data from the server.
     */
    public void clearAll() throws DataAccessException {
        dataAccess.clearAuthData();
    }

    /**
     * Verify that the given auth token is valid.
     *
     * @param authToken The auth token to verify.
     * @return true if the auth token exists in the database; false otherwise.
     */
    public boolean verifyAuth(String authToken) throws DataAccessException {
        try {
            dataAccess.getAuthData(authToken);
        } catch (EntryNotFoundException e) {
            return false;
        }
        return true;
    }
}
