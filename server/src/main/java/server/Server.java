package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.EntryAlreadyExistsException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;
import io.javalin.*;
import io.javalin.http.Context;
import service.AuthService;
import service.GameService;
import service.UserService;

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
