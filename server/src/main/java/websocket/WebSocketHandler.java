package websocket;

import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

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
     * Handle message from client to server.
     *
     * @param ctx The WsConnectContext for the connection.
     */
    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) {
        throw new RuntimeException("Not Implemented"); // TODO
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
