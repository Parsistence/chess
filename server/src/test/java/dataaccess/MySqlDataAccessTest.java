package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.AuthService;

import static org.junit.jupiter.api.Assertions.*;

class MySqlDataAccessTest {
    private MySqlDataAccess dataAccess;
    private AuthService authService;

    @BeforeEach
    void beforeEach() throws DataAccessException {
        dataAccess = new MySqlDataAccess();
        authService = new AuthService(dataAccess);
    }

    @Test
    void clearUsers() throws DataAccessException {
        UserData user1 = randomUser();
        UserData user2 = randomUser();
        UserData user3 = randomUser();

        dataAccess.insertUser(user1);
        dataAccess.insertUser(user2);
        dataAccess.insertUser(user3);

        dataAccess.clearUsers();

        assertThrows(EntryNotFoundException.class, () -> dataAccess.getUser(user1.username()));
        assertThrows(EntryNotFoundException.class, () -> dataAccess.getUser(user2.username()));
        assertThrows(EntryNotFoundException.class, () -> dataAccess.getUser(user3.username()));
    }


    @Test
    void clearAuthData() throws DataAccessException {
        AuthData authData1 = randomAuthData();
        AuthData authData2 = randomAuthData();
        AuthData authData3 = randomAuthData();

        dataAccess.insertAuthData(authData1);
        dataAccess.insertAuthData(authData2);
        dataAccess.insertAuthData(authData3);

        dataAccess.clearAuthData();

        assertThrows(EntryNotFoundException.class, () -> dataAccess.getAuthData(authData1.authToken()));
        assertThrows(EntryNotFoundException.class, () -> dataAccess.getAuthData(authData2.authToken()));
        assertThrows(EntryNotFoundException.class, () -> dataAccess.getAuthData(authData3.authToken()));

    }

    @Test
    void clearGameData() {
    }

    @Test
    void insertUser() {
    }

    @Test
    void getUser() {
    }

    @Test
    void insertAuthData() {
    }

    @Test
    void getAuthData() {
    }

    @Test
    void removeAuth() {
    }

    @Test
    void listGames() {
    }

    @Test
    void createGame() {
    }

    @Test
    void getUserFromAuth() {
    }

    @Test
    void getGame() {
    }

    @Test
    void updateGame() {
    }

    private UserData randomUser() {
        String username = randomString(5);
        String password = randomString(8);
        String email = randomString(5) + "@" + randomString(5) + ".com";
        return new UserData(username, password, email);
    }

    private AuthData randomAuthData() {
        String username = randomString(5);
        String authToken = authService.generateToken();
        return new AuthData(username, authToken);
    }

    private String randomString(int len) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            char c;
            c = (char) (Math.random() * 96 + 32);
            builder.append(c);
        }
        return builder.toString();
    }
}

