package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import io.javalin.*;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import service.AuthService;
import service.GameService;
import service.TeamAlreadyTakenException;
import service.UserService;

import java.util.Collection;

public class Server {

    private final Javalin server;
    private final UserService userService;
    private final AuthService authService;
    private final GameService gameService;
    private final Gson serializer;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));
        DataAccess dataAccess = new MemoryDataAccess();
        authService = new AuthService(dataAccess);
        userService = new UserService(authService, dataAccess);
        gameService = new GameService(dataAccess);
        serializer = new Gson();

        // Register your endpoints and exception handlers here.

        server.delete("db", this::clear);
        server.post("user", this::register);
        server.post("session", this::login);
        server.delete("session", this::logout);
        server.get("game", this::listGames);
        server.post("game", this::createGame);
        server.put("game", this::joinGame);

        server.exception(EntryAlreadyExistsException.class, this::entryAlreadyExistsException);
        server.exception(EntryNotFoundException.class, this::entryNotFoundException);
        server.exception(TeamAlreadyTakenException.class, this::teamAlreadyTakenExceptionHandler);
        server.exception(DataAccessException.class, this::dataAccessExceptionHandler);
    }

    private void joinGame(Context ctx) throws DataAccessException, TeamAlreadyTakenException {
        String authToken = ctx.header("authorization");
        if (!authService.verifyAuth(authToken)) {
            ctx.status(401).result("{ \"message\": \"Error: unauthorized\" }");
            return;
        }

        JoinGameRequest req = serializer.fromJson(ctx.body(), JoinGameRequest.class);
        if (req.playerColor() == null || req.gameID() == 0) {
            ctx.status(400).result("{ \"message\": \"Error: bad request\" }");
            return;
        }

        UserData userData;
        userData = userService.getUser(authToken);
        gameService.joinGame(userData, req.playerColor(), req.gameID());

        ctx.result("{}");
    }

    private void createGame(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");
        if (!authService.verifyAuth(authToken)) {
            ctx.status(401).result("{ \"message\": \"Error: unauthorized\" }");
            return;
        }

        CreateGameRequest req = serializer.fromJson(ctx.body(), CreateGameRequest.class);
        if (req.gameName() == null) {
            ctx.status(400).result("{ \"message\": \"Error: bad request\" }");
            return;
        }

        CreateGameResponse res;
        res = gameService.createGame(req.gameName());
        ctx.result(serializer.toJson(res));
    }

    private void listGames(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");
        if (!authService.verifyAuth(authToken)) {
            ctx.status(401).result("{ \"message\": \"Error: unauthorized\" }");
            return;
        }

        Collection<GameData> games = gameService.listGames();
        ctx.result(String.format("{ \"games\": %s }", serializer.toJson(games)));
    }

    private void logout(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");

        authService.logout(authToken);

        ctx.result("{}");
    }

    private void login(Context ctx) throws DataAccessException {
        LoginRequest req = serializer.fromJson(ctx.body(), LoginRequest.class);

        if (
                req.username() == null ||
                        req.password() == null
        ) {
            ctx.status(400).result("{ \"message\": \"Error: bad request\" }");
            return;
        }

        AuthData authData;
        authData = authService.login(req);
        ctx.result(serializer.toJson(authData));
    }

    private void clear(Context ctx) throws DataAccessException {
        userService.clearAll();
        authService.clearAll();
        gameService.clearAll();

        ctx.result("{}");
    }

    private void register(Context ctx) throws DataAccessException {
        UserData req = serializer.fromJson(ctx.body(), UserData.class);
        if (
                req.username() == null ||
                        req.password() == null ||
                        req.email() == null
        ) {
            ctx.status(400).result("{ \"message\": \"Error: bad request\" }");
            return;
        }

        // call to the service and register
        AuthData res;
        res = userService.register(req);

        ctx.result(serializer.toJson(res));
    }

    private void entryAlreadyExistsException(@NotNull EntryAlreadyExistsException e, @NotNull Context ctx) {
        ctx.status(403).result("{ \"message\": \"Error: already taken\" }");
    }

    private void entryNotFoundException(@NotNull EntryNotFoundException e, @NotNull Context ctx) {
        ctx.status(401).result("{ \"message\": \"Error: unauthorized\" }");
    }

    private void teamAlreadyTakenExceptionHandler(@NotNull TeamAlreadyTakenException e, @NotNull Context ctx) {
        ctx.status(403).result("{ \"message\": \"Error: already taken\" }");
    }

    private void dataAccessExceptionHandler(@NotNull DataAccessException e, @NotNull Context ctx) {
        ctx.status(500).result("{ \"message\": \"Error: " + e + "\" }");
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
