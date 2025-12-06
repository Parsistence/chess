package websocket;

import chess.ChessGame;
import chess.ChessMove;
import dataaccess.*;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class UserCommandHandler {
    private final ConnectionManager connectionManager = new ConnectionManager();
    private final DataAccess dataAccess = new MySqlDataAccess();

    public UserCommandHandler() throws DataAccessException {
    }

    /**
     * Handles a CONNECT message to connect a user to a chess game.
     *
     * @param authToken The auth token of the connecting user.
     * @param gameID    The ID of the chess game to connect to.
     * @param session   The user's session.
     */
    public void handleConnect(String authToken, int gameID, Session session) throws IOException {
        try {
            connectionManager.add(authToken, gameID, session);
            connectionManager.sendGame(session, dataAccess.getGame(gameID).game());
        } catch (DataAccessException e) {
            connectionManager.sendError(session, e.getMessage());
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
    public void handleLeave(String authToken, int gameID, Session session) throws IOException {
        try {
            connectionManager.remove(authToken, gameID, session);
        } catch (DataAccessException e) {
            connectionManager.sendError(session, e.getMessage());
        }
    }

    /**
     * Handles a RESIGN message to the server to resign (but not leave) a game.
     *
     * @param authToken The auth token of the user.
     * @param gameID    The ID of the chess game to leave.
     * @param session   The user's session.
     */
    public void handleResign(String authToken, int gameID, Session session) throws IOException {
        ChessGame.TeamColor teamColor = connectionManager.getTeamColor(session, gameID);
        if (teamColor == null) {
            connectionManager.sendError(session, "Session is not connected as a player for this game.");
            return;
        }

        try {
            dataAccess.getGame(gameID).game().resignTeam(teamColor);
            connectionManager.sendMessage(session, "Successfully resigned from the game.");
            String username = dataAccess.getUserFromAuth(authToken).username();
            connectionManager.broadcastExcluding(username + " resigned from the game.", gameID, session);
        } catch (DataAccessException e) {
            connectionManager.sendError(session, e.getMessage());
        }
    }

}
