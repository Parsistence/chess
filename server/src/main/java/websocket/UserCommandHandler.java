package websocket;

import chess.ChessGame;
import chess.ChessGame.TeamColor;
import chess.ChessGame.WinState;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.*;
import model.GameData;
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
    public void handleMakeMove(String authToken, int gameID, ChessMove move, Session session) throws IOException {
        try {
            if (!connectionManager.getAuthorizedSession(authToken).equals(session)) {
                connectionManager.sendError(session, "Auth token does not match session.");
                return;
            }

            var teamColor = connectionManager.getTeamColor(session, gameID);
            GameData gameData = dataAccess.getGame(gameID);
            ChessGame game = gameData.game();

            TeamColor teamTurnColor = game.getTeamTurn();
            String errorMessage = buildErrorMessage(game, teamColor, teamTurnColor);
            if (errorMessage != null) {
                connectionManager.sendError(session, errorMessage);
                return;
            }

            try {
                game.makeMove(move);
                dataAccess.updateGame(gameID, game);

                connectionManager.broadcastGame(gameID);
                String username = dataAccess.getUserFromAuth(authToken).username();
                connectionManager.broadcastExcluding(username + " has made their move!", gameID, session);

                broadcastGameState(gameID, session, game, gameData);
            } catch (InvalidMoveException e) {
                connectionManager.sendError(session, e.getMessage());
            }

        } catch (DataAccessException e) {
            connectionManager.sendError(session, e.getMessage());
        }
    }

    private void broadcastGameState(int gameID, Session session, ChessGame game, GameData gameData) throws IOException {
        var winState = game.getWinState();
        if (winState != WinState.IN_PROGRESS) {
            broadcastWinState(gameID, session, winState, gameData);
        } else if (game.isInCheck(TeamColor.WHITE)) {
            connectionManager.broadcast(gameData.whiteUsername() + " (White) is in check!", gameID);
        } else if (game.isInCheck(TeamColor.BLACK)) {
            connectionManager.broadcast(gameData.blackUsername() + " (Black) is in check!", gameID);
        }
    }

    /**
     * Broadcast the win state of the chess game to all connected sessions.
     *
     * @param gameID   The ID of the game.
     * @param session  The session of the player that ended the game.
     * @param winState The WinState of the game.
     * @param gameData The GameData for the game.
     * @throws IOException If there was an issue with the websocket communication.
     */
    private void broadcastWinState(int gameID, Session session, WinState winState, GameData gameData) throws IOException {
        String userMessage = null;
        String broadcastMessage = null;
        switch (winState) {
            case WHITE_BEAT_BLACK -> {
                userMessage = "You put " + gameData.blackUsername() + " in checkmate. Congratulations, you win!";
                broadcastMessage = gameData.whiteUsername() + " put " + gameData.blackUsername() + " in checkmate. Game over, White wins!";
            }
            case BLACK_BEAT_WHITE -> {
                userMessage = "You put " + gameData.whiteUsername() + " in checkmate. Congratulations, you win!";
                broadcastMessage = gameData.blackUsername() + " put " + gameData.whiteUsername() + " in checkmate. Game over, Black wins!";
            }
            case STALEMATE -> {
                userMessage = gameData.whiteUsername() + " and " + gameData.blackUsername() + "are in stalemate. Game over!";
                broadcastMessage = userMessage;
            }
            case WHITE_RESIGNED -> {
                userMessage = "You resigned. Game over!";
                broadcastMessage = gameData.whiteUsername() + " resigned. Game over!";
            }
            case BLACK_RESIGNED -> {
                userMessage = "You resigned. Game over!";
                broadcastMessage = gameData.blackUsername() + " resigned. Game over!";
            }
        }
        connectionManager.sendMessage(session, userMessage);
        connectionManager.broadcastExcluding(broadcastMessage, gameID, session);
    }

    /**
     * Build an error message based on the game and session states.
     *
     * @param game          The chess game.
     * @param teamColor     The team color of the session.
     * @param teamTurnColor The team color of the team whose turn it is.
     * @return A string representing the error if an error was encountered; null otherwise.
     */
    private String buildErrorMessage(ChessGame game, TeamColor teamColor, TeamColor teamTurnColor) {
        String errorMessage = null;
        if (teamColor == null) {
            errorMessage = "Session is not connected as a player for this game.";
        } else if (game.getWinState() != WinState.IN_PROGRESS) {
            errorMessage = "Game has ended and no moves can be made.";
        } else if (teamColor != teamTurnColor) {
            errorMessage = "It is not your turn to make a move.";
        }
        return errorMessage;
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
            var gameData = dataAccess.getGame(gameID);
            String username = dataAccess.getUserFromAuth(authToken).username();
            TeamColor teamColor = gameData.getTeamOfPlayer(username);
            var updatedGameData = gameData.withPlayers(
                    (teamColor == TeamColor.WHITE) ? null : gameData.whiteUsername(),
                    (teamColor == TeamColor.BLACK) ? null : gameData.blackUsername()
            );
            dataAccess.updateGameData(gameID, updatedGameData);
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
        try {
            TeamColor teamColor = connectionManager.getTeamColor(session, gameID);
            ChessGame game = dataAccess.getGame(gameID).game();
            if (teamColor == null) {
                connectionManager.sendError(session, "Session is not connected as a player for this game.");
                return;
            } else if (game.getWinState() != WinState.IN_PROGRESS) {
                connectionManager.sendError(session, "Cannot resign because game has ended.");
                return;
            }
            game.resignTeam(teamColor);
            dataAccess.updateGame(gameID, game);
            connectionManager.sendMessage(session, "Successfully resigned from the game.");
            String username = dataAccess.getUserFromAuth(authToken).username();
            connectionManager.broadcastExcluding(username + " resigned from the game.", gameID, session);
        } catch (DataAccessException e) {
            connectionManager.sendError(session, e.getMessage());
        }
    }

}
