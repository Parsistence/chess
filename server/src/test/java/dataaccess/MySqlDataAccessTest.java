package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.AuthService;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class MySqlDataAccessTest {
    private MySqlDataAccess dataAccess;
    private AuthService authService;

    @BeforeEach
    void beforeEach() throws DataAccessException {
        dataAccess = new MySqlDataAccess();
        authService = new AuthService(dataAccess);

        dataAccess.clearUsers();
        dataAccess.clearAuthData();
        dataAccess.clearGameData();
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
    void clearGameData() throws DataAccessException {
        GameData game1 = dataAccess.createGame(randomString(5));
        GameData game2 = dataAccess.createGame(randomString(5));
        GameData game3 = dataAccess.createGame(randomString(5));

        dataAccess.clearGameData();

        assertThrows(EntryNotFoundException.class, () -> dataAccess.getGame(game1.gameID()));
        assertThrows(EntryNotFoundException.class, () -> dataAccess.getGame(game2.gameID()));
        assertThrows(EntryNotFoundException.class, () -> dataAccess.getGame(game3.gameID()));
    }

    @Test
    void insertUser() throws DataAccessException {
        UserData user = randomUser();

        assertDoesNotThrow(() -> dataAccess.insertUser(user));

        UserData savedUser = dataAccess.getUser(user.username());

        assertEquals(user.username(), savedUser.username());
        assertEquals(user.email(), savedUser.email());

        assertTrue(dataAccess.verifyPassword(user.username(), user.password()));
    }

    @Test
    void insertDuplicateUser() {
        UserData user = randomUser();

        assertDoesNotThrow(() -> dataAccess.insertUser(user));
        assertThrows(EntryAlreadyExistsException.class, () -> dataAccess.insertUser(user));
    }

    @Test
    void getUser() throws DataAccessException {
        UserData user = randomUser();

        dataAccess.insertUser(user);

        UserData savedUser = dataAccess.getUser(user.username());

        assertEquals(user.username(), savedUser.username());
        assertEquals(user.email(), savedUser.email());
        assertTrue(dataAccess.verifyPassword(user.username(), user.password()));
    }

    @Test
    void getNonexistentUser() {
        UserData user = randomUser();

        assertThrows(EntryNotFoundException.class, () -> dataAccess.getUser(user.username()));
    }

    @Test
    void verifyPassword() throws DataAccessException {
        UserData user = randomUser();
        dataAccess.insertUser(user);

        assertTrue(dataAccess.verifyPassword(user.username(), user.password()));
    }

    @Test
    void verifyIncorrectPassword() throws DataAccessException {
        UserData user = randomUser();
        dataAccess.insertUser(user);

        String incorrectPassword = randomString(8);
        assertFalse(dataAccess.verifyPassword(user.username(), incorrectPassword));
    }

    @Test
    void insertAuthData() throws DataAccessException {
        AuthData authData = randomAuthData();

        assertDoesNotThrow(() -> dataAccess.insertAuthData(authData));

        AuthData savedAuthData = dataAccess.getAuthData(authData.authToken());

        assertEquals(authData, savedAuthData);
    }

    @Test
    void insertDuplicateAuthData() {
        AuthData authData = randomAuthData();

        assertDoesNotThrow(() -> dataAccess.insertAuthData(authData));
        assertThrows(EntryAlreadyExistsException.class, () -> dataAccess.insertAuthData(authData));

    }

    @Test
    void getAuthData() throws DataAccessException {
        AuthData authData = randomAuthData();
        dataAccess.insertAuthData(authData);

        AuthData savedAuthData = dataAccess.getAuthData(authData.authToken());
        assertEquals(authData, savedAuthData);
    }

    @Test
    void getNonexistentAuthData() {
        AuthData authData = randomAuthData();

        assertThrows(EntryNotFoundException.class, () -> dataAccess.getAuthData(authData.authToken()));
    }

    @Test
    void removeAuth() throws DataAccessException {
        AuthData authData = randomAuthData();
        dataAccess.insertAuthData(authData);
        assertEquals(authData, dataAccess.getAuthData(authData.authToken()));

        assertDoesNotThrow(() -> dataAccess.removeAuth(authData.authToken()));
        assertThrows(EntryNotFoundException.class, () -> dataAccess.getAuthData(authData.authToken()));
    }

    @Test
    void removeNonexistentAuth() {
        AuthData authData = randomAuthData();
        assertThrows(EntryNotFoundException.class, () -> dataAccess.removeAuth(authData.authToken()));
    }

    @Test
    void listGames() throws DataAccessException {
        GameData game1 = dataAccess.createGame("game1");
        GameData game2 = dataAccess.createGame("game2");
        GameData game3 = dataAccess.createGame("game3");

        Collection<GameData> savedGames = dataAccess.listGames();

        assertTrue(savedGames.contains(game1));
        assertTrue(savedGames.contains(game2));
        assertTrue(savedGames.contains(game3));
    }

    @Test
    void createGame() throws DataAccessException {
        GameData game = dataAccess.createGame(randomString(5));
        assertEquals(game, dataAccess.getGame(game.gameID()));
    }

    @Test
    void createDuplicateGame() {
        String gameName = randomString(8);
        assertDoesNotThrow(() -> dataAccess.createGame(gameName));
        assertThrows(EntryAlreadyExistsException.class, () -> dataAccess.createGame(gameName));
    }

    @Test
    void getUserFromAuth() throws DataAccessException {
        UserData user = randomUser();
        dataAccess.insertUser(user);

        AuthData authData = new AuthData(user.username(), authService.generateToken());
        dataAccess.insertAuthData(authData);

        UserData savedUser = dataAccess.getUserFromAuth(authData.authToken());
        assertEquals(user.username(), savedUser.username());
        assertEquals(user.email(), savedUser.email());
        assertTrue(dataAccess.verifyPassword(user.username(), user.password()));
    }

    @Test
    void getUserFromNonexistentAuth() {
        String authToken = authService.generateToken();

        assertThrows(EntryNotFoundException.class, () -> dataAccess.getUserFromAuth(authToken));
    }

    @Test
    void getGame() throws DataAccessException {
        GameData game = dataAccess.createGame(randomString(5));

        GameData savedGame = dataAccess.getGame(game.gameID());
        assertEquals(game, savedGame);
    }

    @Test
    void getNonexistentGame() {
        int gameID = (int) (Math.random() * 1000);
        assertThrows(EntryNotFoundException.class, () -> dataAccess.getGame(gameID));
    }

    @Test
    void updateGame() throws DataAccessException {
        GameData game = dataAccess.createGame(randomString(8));

        String newWhiteUsername = randomString(5);
        String newBlackUsername = randomString(5);
        GameData updatedGame = new GameData(
                game.gameID(),
                newWhiteUsername,
                newBlackUsername,
                game.gameName(),
                game.game()
        );

        assertDoesNotThrow(() -> dataAccess.updateGame(game.gameID(), updatedGame));
        assertEquals(updatedGame, dataAccess.getGame(game.gameID()));
        assertNotEquals(game, dataAccess.getGame(game.gameID()));
    }

    @Test
    void updateNonexistentGame() {
        int gameID = (int) (Math.random() * 1000);

        GameData gameData = new GameData(gameID, randomString(8));

        assertThrows(EntryNotFoundException.class, () -> dataAccess.updateGame(gameID, gameData));
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

