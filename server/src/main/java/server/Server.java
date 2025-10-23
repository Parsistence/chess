package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccess;
import dataaccess.EntryAlreadyExistsException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;
import io.javalin.*;
import io.javalin.http.Context;
import service.AuthService;
import service.UserService;

public class Server {

    private final Javalin server;
    private final UserService userService;
    private final AuthService authService;
    private final Gson serializer;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));
        DataAccess dataAccess = new MemoryDataAccess();
        authService = new AuthService();
        userService = new UserService(authService, dataAccess);
        serializer = new Gson();

        // Register your endpoints and exception handlers here.

        server.delete("db", ctx -> ctx.result("{}"));
        server.post("user", this::register);

    }

    private void register(Context ctx) {
        UserData req = null;
        try {
            req = serializer.fromJson(ctx.body(), UserData.class);
        } catch (JsonSyntaxException e) {
            ctx.status(400).result("{ \"message\": \"Error: bad request\" }");
        }

        // call to the service and register
        AuthData res = null;
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
