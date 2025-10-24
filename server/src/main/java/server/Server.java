package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.EntryAlreadyExistsException;
import dataaccess.EntryNotFoundException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import io.javalin.*;
import io.javalin.http.Context;
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

    }

    private void joinGame(Context ctx) {
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
        try {
            userData = userService.getUser(authToken);
        } catch (EntryNotFoundException e) {
            ctx.status(500).result("{ \"message\": \"Error: " + e + "\" }");
            return;
        }

        try {
            gameService.joinGame(userData, req.playerColor(), req.gameID());
        } catch (TeamAlreadyTakenException e) {
            ctx.status(403).result("{ \"message\": \"Error: already taken\" }");
            return;
        } catch (Exception e) {
            ctx.status(500).result("{ \"message\": \"Error: " + e + "\" }");
            return;
        }

        ctx.result("{}");
    }

    private void createGame(Context ctx) {
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
        try {
            res = gameService.createGame(req.gameName());
        } catch (Exception e) {
            ctx.status(500).result("{ \"message\": \"Error: " + e + "\" }");
            return;
        }
        ctx.result(serializer.toJson(res));
    }

    private void listGames(Context ctx) {
        String authToken = ctx.header("authorization");
        if (!authService.verifyAuth(authToken)) {
            ctx.status(401).result("{ \"message\": \"Error: unauthorized\" }");
            return;
        }

        Collection<GameData> games = gameService.listGames();
        ctx.result(String.format("{ \"games\": %s }", serializer.toJson(games)));
    }

    private void logout(Context ctx) {
        String authToken = ctx.header("authorization");

        try {
            authService.logout(authToken);
        } catch (EntryNotFoundException e) {
            ctx.status(401).result("{ \"message\": \"Error: unauthorized\" }");
            return;
        } catch (Exception e) {
            ctx.status(500).result("{ \"message\": \"Error: " + e + "\" }");
            return;
        }

        ctx.result("{}");
    }

    private void login(Context ctx) {
        LoginRequest req = serializer.fromJson(ctx.body(), LoginRequest.class);

        if (
                req.username() == null ||
                        req.password() == null
        ) {
            ctx.status(400).result("{ \"message\": \"Error: bad request\" }");
            return;
        }

        AuthData authData;
        try {
            authData = authService.login(req);
        } catch (EntryNotFoundException e) {
            ctx.status(401).result("{ \"message\": \"Error: unauthorized\" }");
            return;
        } catch (Exception e) {
            ctx.status(500).result("{ \"message\": \"Error: " + e + "\" }");
            return;
        }

        ctx.result(serializer.toJson(authData));
    }

    private void clear(Context ctx) {
        try {
            userService.clearAll();
            authService.clearAll();
            gameService.clearAll();
        } catch (Exception e) {
            ctx.status(500).result("{ \"message\": \"Error: " + e + "\" }");
            return;
        }

        ctx.result("{}");
    }

    private void register(Context ctx) {
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
        try {
            res = userService.register(req);
        } catch (EntryAlreadyExistsException e) {
            ctx.status(403).result("{ \"message\": \"Error: already taken\" }");
            return;
        } catch (Exception e) {
            ctx.status(500).result("{ \"message\": \"Error: " + e + "\" }");
            return;
        }

        ctx.result(serializer.toJson(res));
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
