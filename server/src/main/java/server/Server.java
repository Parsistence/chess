package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import datamodel.RegistrationResult;
import datamodel.UserData;
import io.javalin.*;
import io.javalin.http.Context;
import service.UserService;

public class Server {

    private final Javalin server;
    private final UserService userService;
    private final DataAccess dataAccess;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));
        dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);

        // Register your endpoints and exception handlers here.

        server.delete("db", ctx -> ctx.result("{}"));
        server.post("user", this::register);

    }

    private void register(Context ctx) {
        var serializer = new Gson();
        UserData req = serializer.fromJson(ctx.body(), UserData.class);

        // call to the service and register
        RegistrationResult res = userService.register(req);

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
