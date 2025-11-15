package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessGame.TeamColor;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Map;

import static ui.EscapeSequences.*;

public class ChessBoardStringRenderer {
    private final Map<ChessPiece, String> pieceStringMap = Map.ofEntries(
            // White
            Map.entry(new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.KING), whiteColor(WHITE_KING)),
            Map.entry(new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.QUEEN), whiteColor(WHITE_QUEEN)),
            Map.entry(new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.BISHOP), whiteColor(WHITE_BISHOP)),
            Map.entry(new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.KNIGHT), whiteColor(WHITE_KNIGHT)),
            Map.entry(new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.ROOK), whiteColor(WHITE_ROOK)),
            Map.entry(new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.PAWN), whiteColor(WHITE_PAWN)),

            // Black
            Map.entry(new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.KING), blackColor(BLACK_KING)),
            Map.entry(new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.QUEEN), blackColor(BLACK_QUEEN)),
            Map.entry(new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.BISHOP), blackColor(BLACK_BISHOP)),
            Map.entry(new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.KNIGHT), blackColor(BLACK_KNIGHT)),
            Map.entry(new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.ROOK), blackColor(BLACK_ROOK)),
            Map.entry(new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.PAWN), blackColor(BLACK_PAWN))
    );

    private final Map<Integer, String> integerFullWidthMap = Map.of(
            1, " １ ",
            2, " ２ ",
            3, " ３ ",
            4, " ４ ",
            5, " ５ ",
            6, " ６ ",
            7, " ７ ",
            8, " ８ "
    );

    private String whiteColor(String text) {
        return SET_TEXT_COLOR_BLUE + text + RESET_TEXT_COLOR;
    }

    private String blackColor(String text) {
        return SET_TEXT_COLOR_MAGENTA + text + RESET_TEXT_COLOR;
    }

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
        var sb = new StringBuilder();

        sb.append(SET_BG_COLOR_LIGHT_GREY).append(EMPTY + " Ａ  Ｂ  Ｃ  Ｄ  Ｅ  Ｆ  Ｇ  Ｈ " + EMPTY).append(RESET_BG_COLOR + "\n");

        for (int r = 8; r > 0; r--) {
            var rowPieces = new String[8];
            for (int c = 1; c <= 8; c++) {
                ChessPiece piece = board.getPiece(new ChessPosition(r, c));
                rowPieces[c - 1] = (piece != null) ? pieceStringMap.get(piece) : EMPTY;
            }
            sb
                    .append(SET_BG_COLOR_LIGHT_GREY).append(integerFullWidthMap.get(r))
                    .append((r % 2 == 0) ? checkeredWhite(rowPieces) : checkeredBlack(rowPieces))
                    .append(SET_BG_COLOR_LIGHT_GREY).append(integerFullWidthMap.get(r)).append(RESET_BG_COLOR + "\n")
            ;
        }

        sb.append(SET_BG_COLOR_LIGHT_GREY).append(EMPTY + " Ａ  Ｂ  Ｃ  Ｄ  Ｅ  Ｆ  Ｇ  Ｈ " + EMPTY).append(RESET_BG_COLOR + "\n");

        return sb.toString();
    }

    private String checkeredWhite(String[] rowPieces) {
        var sb = new StringBuilder();

        for (int i = 0; i < rowPieces.length; i++) {
            String piece = rowPieces[i];
            sb
                    .append((i % 2 == 0) ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK)
                    .append(piece)
            ;
        }
        sb.append(RESET_BG_COLOR);

        return sb.toString();
    }

    private String renderBoardBlack(ChessBoard board) {
        var sb = new StringBuilder();

        sb.append(SET_BG_COLOR_LIGHT_GREY).append(EMPTY + " Ｈ  Ｇ  Ｆ  Ｅ  Ｄ  Ｃ  Ｂ  Ａ " + EMPTY).append(RESET_BG_COLOR + "\n");

        for (int r = 1; r <= 8; r++) {
            var rowPieces = new String[8];
            for (int c = 1; c <= 8; c++) {
                ChessPiece piece = board.getPiece(new ChessPosition(r, c));
                rowPieces[8 - c] = (piece != null) ? pieceStringMap.get(piece) : EMPTY;
            }
            sb
                    .append(SET_BG_COLOR_LIGHT_GREY).append(integerFullWidthMap.get(r))
                    .append((r % 2 == 1) ? checkeredWhite(rowPieces) : checkeredBlack(rowPieces))
                    .append(SET_BG_COLOR_LIGHT_GREY).append(integerFullWidthMap.get(r)).append(RESET_BG_COLOR + "\n")
            ;
        }

        sb.append(SET_BG_COLOR_LIGHT_GREY).append(EMPTY + " Ｈ  Ｇ  Ｆ  Ｅ  Ｄ  Ｃ  Ｂ  Ａ " + EMPTY).append(RESET_BG_COLOR + "\n");

        return sb.toString();
    }

    private String checkeredBlack(String[] rowPieces) {
        var sb = new StringBuilder();

        for (int i = 0; i < rowPieces.length; i++) {
            String piece = rowPieces[i];
            sb
                    .append((i % 2 == 1) ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK)
                    .append(piece)
            ;
        }
        sb.append(RESET_BG_COLOR);

        return sb.toString();
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
