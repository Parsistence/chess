package websocket;

import chess.ChessGame;
import chess.ChessGame.TeamColor;
import com.google.gson.Gson;
import dataaccess.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public ConnectionManager() throws DataAccessException {
    }

    public enum UserType {
        WHITE_PLAYER, BLACK_PLAYER, OBSERVER
    }

    private final ConcurrentHashMap<Integer, HashMap<Session, UserType>> connections = new ConcurrentHashMap<>();
    private final DataAccess dataAccess = new MySqlDataAccess();

    private HashMap<Session, UserType> getGameConnections(int gameID) {
        return connections.computeIfAbsent(gameID, k -> new HashMap<>());
    }

    /**
     * Get all sessions that belong to a game.
     *
     * @param gameID The ID of the chess game.
     * @return All sessions currently connected to the game.
     */
    public Collection<Session> getGameSessions(int gameID) {
        return getGameConnections(gameID).keySet();
    }

    public TeamColor getTeamColor(Session session, int gameID) {
        return switch (getGameConnections(gameID).get(session)) {
            case WHITE_PLAYER -> TeamColor.WHITE;
            case BLACK_PLAYER -> TeamColor.BLACK;
            default -> null;
        };
    }

    /**
     * Adds an authenticated user to an existing game and calls the given callback function.
     *
     * @param authToken The auth token of the user.
     * @param gameID    The ID of the chess game to join.
     * @throws EntryNotFoundException If the authenticated user is not found in the database.
     */
    public void add(String authToken, int gameID, Session session) throws DataAccessException, IOException {
        String username = dataAccess.getUserFromAuth(authToken).username();
        GameData game = dataAccess.getGame(gameID);

        UserType userType;
        if (game.whiteUsername().equals(username)) {
            userType = UserType.WHITE_PLAYER;
        } else if (game.blackUsername().equals(username)) {
            userType = UserType.BLACK_PLAYER;
        } else {
            userType = UserType.OBSERVER;
        }

        getGameConnections(gameID).put(session, userType);

        String userTypeDescription = switch (userType) {
            case WHITE_PLAYER -> "the white player";
            case BLACK_PLAYER -> "the black player";
            case OBSERVER -> "an observer";
        };
        broadcastExcluding(username + "joined the game as " + userTypeDescription + ".", gameID, session);
    }

    public void remove(String authToken, int gameID, Session session) throws DataAccessException, IOException {
        getGameConnections(gameID).remove(session);
        sendMessage(session, "You left the game.");
        String username = dataAccess.getUserFromAuth(authToken).username();
        broadcastExcluding(username + " left the game.", gameID, session);
    }

    /**
     * Broadcasts a message to all users connected to a given game, optionally excluding a session.
     *
     * @param message         The message to broadcast.
     * @param gameID          The game ID a session must be connected with to receive the broadcast.
     * @param excludedSession (Optional) The session to exclude from the broadcast.
     */
    public void broadcastExcluding(String message, int gameID, Session excludedSession) throws IOException {
        for (var session : getGameSessions(gameID)) {
            if (!session.equals(excludedSession)) {
                sendMessage(session, message);
            }
        }
    }

    void sendMessage(Session session, String message) throws IOException {
        var notification = new NotificationMessage(message);
        session.getRemote().sendString(new Gson().toJson(notification));
    }

    void sendGame(Session session, ChessGame game) throws IOException {
        var loadGameMessage = new LoadGameMessage(game);
        session.getRemote().sendString(new Gson().toJson(loadGameMessage));
    }

    void sendError(Session session, String errorMessage) throws IOException {
        var errorServerMessage = new ErrorMessage(errorMessage);
        session.getRemote().sendString(new Gson().toJson(errorServerMessage));
    }
}
