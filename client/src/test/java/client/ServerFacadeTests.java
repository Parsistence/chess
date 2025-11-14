package client;

import dataaccess.DataAccessException;
import dataaccess.EntryNotFoundException;
import dataaccess.MySqlDataAccess;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.GameDataList;
import server.ResponseException;
import server.Server;
import server.ServerFacade;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {
    private static Server server;
    private static ServerFacade facade;
    private static MySqlDataAccess dataAccess;

    @BeforeAll
    public static void init() throws DataAccessException {
        server = new Server();
        var port = server.run(0);
        String serverUrl = "http://localhost:" + port;

        facade = new ServerFacade(serverUrl);
        System.out.println("Started test HTTP server on " + port);

        dataAccess = new MySqlDataAccess();
        dataAccess.clearUsers();
        dataAccess.clearAuthData();
        dataAccess.clearGameData();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    void register() throws ResponseException {
        UserData user = randomUser();
        String authToken = facade.register(user.username(), user.password(), user.email());
        assertFalse(authToken.isBlank());

        assertDoesNotThrow(() -> dataAccess.getUserFromAuth(authToken));
    }

    @Test
    void registerDuplicateUser() {
        UserData user = randomUser();
        assertDoesNotThrow(() ->
                facade.register(user.username(), user.password(), user.email())
        );
        assertThrows(ResponseException.class, () ->
                facade.register(user.username(), user.password(), user.email())
        );
    }

    @Test
    void login() throws ResponseException {
        UserData user = randomUser();
        facade.register(user.username(), user.password(), user.email());

        String authToken = facade.login(user.username(), user.password());
        assertDoesNotThrow(() -> dataAccess.getUserFromAuth(authToken));
    }

    @Test
    void loginWrongPassword() throws ResponseException {
        UserData user = randomUser();
        facade.register(user.username(), user.password(), user.email());

        String wrongPassword = "wrong_" + user.password();
        assertThrows(ResponseException.class, () -> facade.login(user.username(), wrongPassword));
    }

    @Test
    void logout() throws ResponseException {
        UserData user = randomUser();
        String authToken = facade.register(user.username(), user.password(), user.email());

        assertDoesNotThrow(() -> dataAccess.getUserFromAuth(authToken));
        assertDoesNotThrow(() -> facade.logout(authToken));
        assertThrows(EntryNotFoundException.class, () -> dataAccess.getUserFromAuth(authToken));
    }

    @Test
    void logoutUserNotLoggedIn() throws ResponseException {
        UserData user = randomUser();
        String authToken = facade.register(user.username(), user.password(), user.email());
        assertDoesNotThrow(() -> dataAccess.getUserFromAuth(authToken));

        assertDoesNotThrow(() -> facade.logout(authToken)); // Log out
        assertThrows(ResponseException.class, () -> facade.logout(authToken)); // Should already be logged out
    }

    @Test
    void listGames() throws DataAccessException, ResponseException {
        // Add some games to database
        GameData game1 = dataAccess.createGame("1_" + randomString(8));
        GameData game2 = dataAccess.createGame("2_" + randomString(8));
        GameData game3 = dataAccess.createGame("3_" + randomString(8));

        // Register a user
        UserData user = randomUser();
        String authToken = facade.register(user.username(), user.password(), user.email());

        // Get list of games
        Collection<GameData> games = facade.listGames(authToken);

        // Assert each game is in list
        assertTrue(games.contains(game1));
        assertTrue(games.contains(game2));
        assertTrue(games.contains(game3));
    }

    @Test
    void listGamesWithoutAuth() throws DataAccessException, ResponseException {
        // Add some games to database
        GameData game1 = dataAccess.createGame("1_" + randomString(8));
        GameData game2 = dataAccess.createGame("2_" + randomString(8));
        GameData game3 = dataAccess.createGame("3_" + randomString(8));

        // Register a user and log out
        UserData user = randomUser();
        String authToken = facade.register(user.username(), user.password(), user.email());
        facade.logout(authToken);

        // Shouldn't get list of games with bad auth
        assertThrows(ResponseException.class, () -> facade.listGames(authToken));
    }

    @Test
    void createGame() {
    }

    @Test
    void joinGame() {
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
