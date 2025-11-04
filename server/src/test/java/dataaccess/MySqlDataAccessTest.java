package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MySqlDataAccessTest {
    private MySqlDataAccess dataAccess;

    @BeforeEach
    void beforeEach() throws DataAccessException {
        dataAccess = new MySqlDataAccess();
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

        Assertions.assertThrows(EntryNotFoundException.class, () -> dataAccess.getUser(user1.username()));
        Assertions.assertThrows(EntryNotFoundException.class, () -> dataAccess.getUser(user2.username()));
        Assertions.assertThrows(EntryNotFoundException.class, () -> dataAccess.getUser(user3.username()));
    }


    @Test
    void clearAuthData() {
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

