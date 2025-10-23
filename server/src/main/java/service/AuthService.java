package service;

import dataaccess.DataAccess;
import dataaccess.EntryAlreadyExistsException;
import model.AuthData;
import model.UserData;

import javax.xml.crypto.Data;
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
     * Clear all auth data from the server.
     */
    public void clearAll() {
        dataAccess.clearAuthData();
    }
}
