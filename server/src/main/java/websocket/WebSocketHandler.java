package websocket;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    UserCommandHandler commandHandler = new UserCommandHandler();

    /**
     * Handle websocket connection from client to server.
     *
     * @param ctx The WsConnectContext for the connection.
     */
    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) {
        System.out.println("New WebSocket connected");
        ctx.enableAutomaticPings();
    }

    /**
     * Handle user game command from client to server.
     *
     * @param ctx The WsConnectContext for the connection.
     */
    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) {
        var userGameCommand = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case CONNECT -> commandHandler.handleConnect(userGameCommand.getAuthToken(), userGameCommand.getGameID());
            case MAKE_MOVE -> {
                var makeMoveCommand = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);
                commandHandler.handleMakeMove(
                        makeMoveCommand.getAuthToken(),
                        makeMoveCommand.getGameID(),
                        makeMoveCommand.getMove()
                );
            }
            case LEAVE -> commandHandler.handleLeave(userGameCommand.getAuthToken(), userGameCommand.getGameID());
            case RESIGN -> commandHandler.handleResign(userGameCommand.getAuthToken(), userGameCommand.getGameID());
        }
    }

    /**
     * Handle websocket connection closure from client to server.
     *
     * @param ctx The WsConnectContext for the connection.
     */
    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("WebSocket connection closed.");
    }
}
