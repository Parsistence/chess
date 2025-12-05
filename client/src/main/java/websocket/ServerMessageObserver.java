package websocket;

import chess.ChessGame;
import model.GameData;

public interface ServerMessageObserver {
    /**
     * Loads a game from the server.
     *
     * @param game The game data to load.
     */
    void loadGame(ChessGame game);

    /**
     * Display an error message from the server.
     *
     * @param errorMessage The error from the server.
     */
    void notifyError(String errorMessage);

    /**
     * Display a notification from the server.
     *
     * @param message notification from the server.
     */
    void notify(String message);


}
