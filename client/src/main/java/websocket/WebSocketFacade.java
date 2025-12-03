package websocket;

import chess.ChessMove;

public class WebSocketFacade {
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
