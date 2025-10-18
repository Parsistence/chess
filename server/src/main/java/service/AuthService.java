package service;

import java.util.UUID;

public class AuthService {
    public String generateToken() {
        return UUID.randomUUID().toString();
    }
}
