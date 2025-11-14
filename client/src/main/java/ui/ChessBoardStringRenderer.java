package ui;

import chess.ChessBoard;
import chess.ChessGame.TeamColor;

public class ChessBoardStringRenderer {
    /**
     * Render a ChessBoard object as a string, from the perspective of the given team color.
     *
     * @param board The board to render.
     * @return The string representation of the chess board.
     */
    public String renderBoard(ChessBoard board) {
        return renderBoard(board, TeamColor.WHITE);
    }

    /**
     * Render a ChessBoard object as a string, from the perspective of the given team color.
     *
     * @param board     The board to render.
     * @param teamColor The perspective to render the board from
     * @return The string representation of the chess board.
     */
    public String renderBoard(ChessBoard board, TeamColor teamColor) {
        return "Board renders here!";
    }
}
