package service;

import dataaccess.DataAccess;
import dataaccess.EntryAlreadyExistsException;
import dataaccess.EntryNotFoundException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;
import server.LoginRequest;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    @Test
    void generateToken() {
        DataAccess dataAccess = new MemoryDataAccess();
        var authService = new AuthService(dataAccess);

        String authToken = authService.generateToken();

        assertNotNull(authToken);
        assertFalse(authToken.isEmpty());
    }

    @Test
    void createAuth() throws EntryAlreadyExistsException, EntryNotFoundException {
        DataAccess dataAccess = new MemoryDataAccess();
        var authService = new AuthService(dataAccess);

        var user = new UserData(
                "bob_java",
                "kotlingoblin",
                "bjava@jmail.com"
        );

        AuthData authData = authService.createAuth(user);
        AuthData savedAuthData = dataAccess.getAuthData(authData.authToken());

        assertEquals(savedAuthData, authData);
    }

    // Note: AuthService#createAuth throws EntryAlreadyExistsException, but this only happens
    // If two UUIDs are equal to each other. There is no way to test this behavior with createAuth
    // at the moment.

    @Test
    void login() throws EntryAlreadyExistsException, EntryNotFoundException {
        DataAccess dataAccess = new MemoryDataAccess();
        var authService = new AuthService(dataAccess);

        var user = new UserData(
                "bob_java",
                "kotlingoblin",
                "bjava@jmail.com"
        );
        dataAccess.insertUser(user);

        var req = new LoginRequest(
                "bob_java",
                "kotlingoblin"
        );
        AuthData authData = authService.login(req);

        UserData savedUser = dataAccess.getUserFromAuth(authData.authToken());
        assertEquals(savedUser, user);
    }

    @Test
    void incorrectPassword() {
        DataAccess dataAccess = new MemoryDataAccess();
        var authService = new AuthService(dataAccess);

        var user = new UserData(
                "bob_java",
                "kotlingoblin",
                "bjava@jmail.com"
        );
        assertDoesNotThrow(() -> dataAccess.insertUser(user));

        var req = new LoginRequest(
                "bob_java",
                "notmypassword"
        );
        assertThrows(EntryNotFoundException.class, () -> authService.login(req));
    }

    @Test
    void logout() throws EntryAlreadyExistsException {
        DataAccess dataAccess = new MemoryDataAccess();
        var authService = new AuthService(dataAccess);
        var userService = new UserService(authService, dataAccess);

        var user = new UserData(
                "bob_java",
                "kotlingoblin",
                "bjava@jmail.com"
        );

        AuthData authData = userService.register(user);

        assertDoesNotThrow(() -> dataAccess.getUserFromAuth(authData.authToken()));

        assertDoesNotThrow(() -> authService.logout(authData.authToken()));

        assertThrows(EntryNotFoundException.class, () -> dataAccess.getUserFromAuth(authData.authToken()));
    }

    @Test
    void unauthorizedLogout() throws EntryAlreadyExistsException {
        DataAccess dataAccess = new MemoryDataAccess();
        var authService = new AuthService(dataAccess);
        var userService = new UserService(authService, dataAccess);

        var user = new UserData(
                "bob_java",
                "kotlingoblin",
                "bjava@jmail.com"
        );

        AuthData authData = userService.register(user);

        assertDoesNotThrow(() -> dataAccess.getUserFromAuth(authData.authToken()));

        String wrongAuthToken = "bogus";

        assertThrows(EntryNotFoundException.class, () -> authService.logout(wrongAuthToken));
    }

    @Test
    void clearAll() throws EntryAlreadyExistsException, EntryNotFoundException {
        DataAccess dataAccess = new MemoryDataAccess();
        AuthService authService = new AuthService(dataAccess);
        UserService userService = new UserService(authService, dataAccess);

        UserData user1 = new UserData(
                "pawnpromoter",
                "queensandknights",
                "pawnp@jmail.com"
        );
        UserData user2 = new UserData(
                "queenrules",
                "kingsarecool",
                "queen@jmail.com"
        );
        UserData user3 = new UserData(
                "ilovechess",
                "yesido",
                "chesslove@hotflash.com"
        );

        AuthData auth1 = userService.register(user1);
        AuthData auth2 = userService.register(user2);
        AuthData auth3 = userService.register(user3);

        assertNotNull(dataAccess.getUserFromAuth(auth1.authToken()));
        assertNotNull(dataAccess.getUserFromAuth(auth2.authToken()));
        assertNotNull(dataAccess.getUserFromAuth(auth3.authToken()));

        authService.clearAll();

        assertThrows(EntryNotFoundException.class, () -> dataAccess.getUserFromAuth(auth1.authToken()));
        assertThrows(EntryNotFoundException.class, () -> dataAccess.getUserFromAuth(auth2.authToken()));
        assertThrows(EntryNotFoundException.class, () -> dataAccess.getUserFromAuth(auth3.authToken()));
    }

    @Test
    void verifyAuth() throws EntryAlreadyExistsException {
        DataAccess dataAccess = new MemoryDataAccess();
        AuthService authService = new AuthService(dataAccess);
        UserService userService = new UserService(authService, dataAccess);

        UserData user = new UserData(
                "bob_java",
                "kotlingoblin",
                "bjava@jmail.com"
        );

        AuthData authData = userService.register(user);

        assertTrue(authService.verifyAuth(authData.authToken()));
    }

    @Test
    void verifyAuthExpired() throws EntryAlreadyExistsException {
        DataAccess dataAccess = new MemoryDataAccess();
        AuthService authService = new AuthService(dataAccess);
        UserService userService = new UserService(authService, dataAccess);

        UserData user = new UserData(
                "bob_java",
                "kotlingoblin",
                "bjava@jmail.com"
        );

        AuthData authData = userService.register(user);
        String authToken = authData.authToken();

        assertTrue(authService.verifyAuth(authToken));

        assertDoesNotThrow(() -> authService.logout(authToken));

        assertFalse(authService.verifyAuth(authToken));
    }
}