package websocket;

import com.google.gson.Gson;
import jakarta.websocket.MessageHandler;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class ServerMessageHandler implements MessageHandler.Whole<String> {
    private final ServerMessageObserver messageObserver;

    public ServerMessageHandler(ServerMessageObserver messageObserver) {
        this.messageObserver = messageObserver;
    }

    /**
     * Handles all ServerMessages received from the server.
     *
     * @param msg The message received from the server.
     */
    @Override
    public void onMessage(String msg) {
        var serverMessage = new Gson().fromJson(msg, ServerMessage.class);
        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME -> {
                var loadGameMessage = new Gson().fromJson(msg, LoadGameMessage.class);
                messageObserver.loadGame(loadGameMessage.getGame());
            }
            case ERROR -> {
                var errorMessage = new Gson().fromJson(msg, ErrorMessage.class);
                messageObserver.notifyError(errorMessage.getErrorMessage());
            }
            case NOTIFICATION -> {
                var notificationMessage = new Gson().fromJson(msg, NotificationMessage.class);
                messageObserver.notify(notificationMessage.getMessage());
            }
        }
    }
}
