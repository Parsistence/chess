package client;

import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import model.UserData;
import org.junit.jupiter.api.*;
import server.ResponseException;
import server.Server;
import server.ServerFacade;

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
    void logout() {
    }

    @Test
    void listGames() {
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
