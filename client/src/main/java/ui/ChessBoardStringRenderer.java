package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessGame.TeamColor;

import static ui.EscapeSequences.*;

public class ChessBoardStringRenderer {
    /**
     * Render a ChessBoard object as a string, from the perspective of the given team color.
     *
     * @param board The board to render.
     * @return The string representation of the chess board.
     */
    public String renderBoard(ChessBoard board) {
        return renderBoardWhite(board);
    }

    /**
     * Render a ChessBoard object as a string, from the perspective of the given team color.
     *
     * @param board     The board to render.
     * @param teamColor The perspective to render the board from
     * @return The string representation of the chess board.
     */
    public String renderBoard(ChessBoard board, TeamColor teamColor) {
        return switch (teamColor) {
            case WHITE -> renderBoardWhite(board);
            case BLACK -> renderBoardBlack(board);
        };
    }

    private String renderBoardWhite(ChessBoard board) {
        return null;
    }

    private String renderBoardBlack(ChessBoard board) {
        return null;
    }

    public static void main(String[] args) {
        var boardStringRenderer = new ChessBoardStringRenderer();
        var game = new ChessGame();

        System.out.println("White perspective:");
        System.out.println(boardStringRenderer.renderBoard(game.getBoard(), TeamColor.WHITE));

        System.out.println();
        System.out.println("Black perspective:");
        System.out.println(boardStringRenderer.renderBoard(game.getBoard(), TeamColor.BLACK));
    }
}
