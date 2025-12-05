package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.EntryNotFoundException;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;

public class UserCommandHandler {
    private final ConnectionManager connectionManager = new ConnectionManager();

    /**
     * Handles a CONNECT message to connect a user to a chess game.
     *
     * @param authToken The auth token of the connecting user.
     * @param gameID    The ID of the chess game to connect to.
     * @param session   The user's session.
     */
    public void handleConnect(String authToken, int gameID, Session session) throws IOException {
        try {
            connectionManager.add(authToken, gameID, (username, userType) -> {
                String userTypeDescription = switch (userType) {
                    case WHITE_PLAYER -> "the white player";
                    case BLACK_PLAYER -> "the black player";
                    case OBSERVER -> "an observer";
                };
                broadcastExcluding(username + "joined the game as " + userTypeDescription + ".", session);
            });
        } catch (EntryNotFoundException e) {
            sendError(session, e.getMessage());
        }
    }

    /**
     * Handles a MAKE_MOVE message to make a user's move in a chess game.
     *
     * @param authToken The auth token of the user.
     * @param gameID    The ID of the chess game to make the move on.
     * @param move      The move to make.
     * @param session   The user's session.
     */
    public void handleMakeMove(String authToken, int gameID, ChessMove move, Session session) {
        throw new RuntimeException("Not implemented."); // TODO
    }

    /**
     * Handles a LEAVE message to leave a game.
     *
     * @param authToken The auth token of the user.
     * @param gameID    The ID of the chess game to leave.
     * @param session   The user's session.
     */
    public void handleLeave(String authToken, int gameID, Session session) {
        connectionManager.remove(authToken, gameID);
    }

    /**
     * Handles a RESIGN message to the server to resign (but not leave) a game.
     *
     * @param authToken The auth token of the user.
     * @param gameID    The ID of the chess game to leave.
     * @param session   The user's session.
     */
    public void handleResign(String authToken, int gameID, Session session) {
        throw new RuntimeException("Not implemented."); // TODO
    }

    /**
     * Broadcasts a message to all users connected to a given game, optionally excluding a session.
     *
     * @param message          The message to broadcast.
     * @param excludingSession (Optional) The session to exclude from the broadcast.
     */
    public void broadcastExcluding(String message, Session excludingSession) {
        throw new RuntimeException("Not implemented."); // TODO
    }

    private void sendMessage(Session session, String message) throws IOException {
        var notification = new NotificationMessage(message);
        session.getRemote().sendString(new Gson().toJson(notification));
    }

    private void sendError(Session session, String errorMessage) throws IOException {
        var errorServerMessage = new ErrorMessage(errorMessage);
        session.getRemote().sendString(new Gson().toJson(errorServerMessage));
    }

    private void sendGame(Session session, ChessGame game) throws IOException {
        var loadGameMessage = new LoadGameMessage(game);
        session.getRemote().sendString(new Gson().toJson(loadGameMessage));
    }
}
