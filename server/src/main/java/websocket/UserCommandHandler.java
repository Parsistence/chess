package websocket;

import chess.ChessMove;

public class UserCommandHandler {
    /**
     * Handles a CONNECT message to connect a user to a chess game.
     *
     * @param authToken The auth token of the connecting user.
     * @param gameID    The ID of the chess game to connect to.
     */
    public void handleConnect(String authToken, int gameID) {
        throw new RuntimeException("Not implemented."); // TODO
    }

    /**
     * Handles a MAKE_MOVE message to make a user's move in a chess game.
     *
     * @param authToken The auth token of the user.
     * @param gameID    The ID of the chess game to make the move on.
     * @param move      The move to make.
     */
    public void handleMakeMove(String authToken, int gameID, ChessMove move) {
        throw new RuntimeException("Not implemented."); // TODO
    }

    /**
     * Handles a LEAVE message to leave a game.
     *
     * @param authToken The auth token of the user.
     * @param gameID    The ID of the chess game to leave.
     */
    public void handleLeave(String authToken, int gameID) {
        throw new RuntimeException("Not implemented."); // TODO
    }

    /**
     * Handles a RESIGN message to the server to resign (but not leave) a game.
     *
     * @param authToken The auth token of the user.
     * @param gameID    The ID of the chess game to leave.
     */
    public void handleResign(String authToken, int gameID) {
        throw new RuntimeException("Not implemented."); // TODO
    }
}
