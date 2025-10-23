package service;

import dataaccess.DataAccess;

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
     * Clear all auth data from the server.
     */
    public void clearAll() {
        dataAccess.clearAuthData();
    }
}
