package websocket;

import jakarta.websocket.MessageHandler;

public class ServerMessageHandler implements MessageHandler.Whole<String> {
    /**
     * Handles all ServerMessages received from the server.
     *
     * @param msg The message received from the server.
     */
    @Override
    public void onMessage(String msg) {
        throw new RuntimeException("Not implemented"); // TODO
    }
}
