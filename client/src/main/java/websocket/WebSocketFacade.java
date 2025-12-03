package websocket;

import chess.ChessMove;
import jakarta.websocket.*;
import server.ResponseException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade {
    private final Session session;

    public WebSocketFacade(String serverUrl) throws ResponseException {
        try {
            String wsUrl = serverUrl.replace("http", "ws");
            URI socketUri = new URI(wsUrl + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            session = container.connectToServer(this, socketUri);

            session.addMessageHandler((MessageHandler.Whole<String>) msg -> {
                throw new RuntimeException("Not implemented"); // TODO
            });
        } catch (URISyntaxException | DeploymentException | IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Sends a CONNECT message to the server to join a chess game.
     *
     * @param authToken The auth token of the connecting user.
     * @param gameID    The ID of the chess game to connect to.
     */
    public void connect(String authToken, int gameID) {
        throw new RuntimeException("Not implemented."); // TODO
    }

    /**
     * Sends a MAKE_MOVE message to the server to make a move in a chess game.
     *
     * @param authToken The auth token of the user.
     * @param gameID    The ID of the chess game to make the move on.
     * @param move      The move to make.
     */
    public void makeMove(String authToken, int gameID, ChessMove move) {
        throw new RuntimeException("Not implemented."); // TODO
    }

    /**
     * Sends a LEAVE message to the server to leave a game.
     *
     * @param authToken The auth token of the user.
     * @param gameID    The ID of the chess game to leave.
     */
    public void leave(String authToken, int gameID) {
        throw new RuntimeException("Not implemented."); // TODO
    }

    /**
     * Sends a RESIGN message to the server to resign (but not leave) a game.
     *
     * @param authToken The auth token of the user.
     * @param gameID    The ID of the chess game to leave.
     */
    public void resign(String authToken, int gameID) {
        throw new RuntimeException("Not implemented."); // TODO
    }
}
