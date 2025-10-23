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
    void register() throws EntryNotFoundException {
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

    @Test
    void clearAll() {
        DataAccess dataAccess = new MemoryDataAccess();
        AuthService authService = new AuthService(dataAccess);
        UserService userService = new UserService(authService, dataAccess);

        UserData user1 = new UserData(
                "bob_java",
                "kotlingoblin",
                "bjava@jmail.com"
        );
        UserData user2 = new UserData(
                "jane_java",
                "jayjay",
                "jjava@jmail.com"
        );
        UserData user3 = new UserData(
                "walt_whitman",
                "stars",
                "whitman@hotflash.com"
        );

        assertDoesNotThrow(() -> {
            userService.register(user1);
            userService.register(user2);
            userService.register(user3);
        });

        userService.clearAll();

        assertDoesNotThrow(() -> {
            userService.register(user1);
            userService.register(user2);
            userService.register(user3);
        });
    }
}