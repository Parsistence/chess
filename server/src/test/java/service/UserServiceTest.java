package service;

import dataaccess.DataAccess;
import dataaccess.EntryAlreadyExistsException;
import dataaccess.EntryNotFoundException;
import dataaccess.MemoryDataAccess;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void registerNewUser() throws EntryNotFoundException {
        DataAccess dataAccess = new MemoryDataAccess();
        AuthService authService = new AuthService(dataAccess);
        UserService userService = new UserService(authService, dataAccess);

        UserData user = new UserData(
                "bob_java",
                "kotlingoblin",
                "bjava@jmail.com"
        );

        // Make sure user is not in database
        assertThrows(EntryNotFoundException.class, () -> dataAccess.getUser(user.username()));

        // register user
        assertDoesNotThrow(() -> userService.register(user));

        // Assert user is in database
        UserData savedUser = dataAccess.getUser(user.username());

        assertEquals(user, savedUser);
    }

    @Test
    void usernameAlreadyTaken() {
        DataAccess dataAccess = new MemoryDataAccess();
        AuthService authService = new AuthService(dataAccess);
        UserService userService = new UserService(authService, dataAccess);

        UserData user = new UserData(
                "bob_java",
                "kotlingoblin",
                "bjava@jmail.com"
        );

        assertDoesNotThrow(() -> userService.register(user));
        assertThrows(EntryAlreadyExistsException.class, () -> userService.register(user));
    }
}