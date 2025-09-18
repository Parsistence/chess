package chess;

import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {
        resetBoard();
    }

    public ChessBoard(ChessBoard otherBoard) {
        setBoard(otherBoard.board);
    }

    /**
     * Checks a given position to see if it is on the board.
     * @param pos The position to check.
     * @return true if pos is on the board; false otherwise.
     */
    public boolean posInBounds(ChessPosition pos) {
        try {
            var _ = board[pos.getRow() - 1][pos.getColumn() - 1];
        } catch (ArrayIndexOutOfBoundsException _ ) {
            return false;
        }
        return true;
    }

    /**
     * Gets the position of the corner farthest from the corner at (1,1).
     * This can be helpful for accessing the length and width of the board.
     * @return the ChessPosition of the farthest corner.
     */
    public ChessPosition getFarthestPos() {
        return new ChessPosition(
                board.length,
                board[0].length
        );
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        try {
            board[position.getRow() - 1][position.getColumn() - 1] = piece;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException(
                    String.format("The given position (%s) is located outside the chessboard's range.", position)
            );
        }
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        ChessPiece piece;
        try {
            piece = board[position.getRow() - 1][position.getColumn() - 1];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException(
                    String.format("The given position (%s) is located outside the chessboard's range.", position)
            );
        }
        return piece;
    }

    /**
     * Sets the board to the values in newBoard.
     *
     * @param newBoard the new board to copy into board.
     */
    public void setBoard(ChessPiece[][] newBoard) {
        System.arraycopy(board, 0, newBoard, 0, board.length);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        var defaultBoard = new ChessPiece[8][8];

        // Pawn rows
        var whitePawns = defaultBoard[1];
        var blackPawns = defaultBoard[6];
        for (int i = 0; i < whitePawns.length; i++) {
            whitePawns[i] = new ChessPiece(TeamColor.WHITE, PieceType.PAWN);
            blackPawns[i] = new ChessPiece(TeamColor.BLACK, PieceType.PAWN);
        }

        // King + Elites rows
        defaultBoard[0] = new ChessPiece[]{
                new ChessPiece(TeamColor.WHITE, PieceType.ROOK),
                new ChessPiece(TeamColor.WHITE, PieceType.KNIGHT),
                new ChessPiece(TeamColor.WHITE, PieceType.BISHOP),
                new ChessPiece(TeamColor.WHITE, PieceType.QUEEN),
                new ChessPiece(TeamColor.WHITE, PieceType.KING),
                new ChessPiece(TeamColor.WHITE, PieceType.BISHOP),
                new ChessPiece(TeamColor.WHITE, PieceType.KNIGHT),
                new ChessPiece(TeamColor.WHITE, PieceType.ROOK),
        };
        defaultBoard[7] = new ChessPiece[]{
                new ChessPiece(TeamColor.BLACK, PieceType.ROOK),
                new ChessPiece(TeamColor.BLACK, PieceType.KNIGHT),
                new ChessPiece(TeamColor.BLACK, PieceType.BISHOP),
                new ChessPiece(TeamColor.BLACK, PieceType.QUEEN),
                new ChessPiece(TeamColor.BLACK, PieceType.KING),
                new ChessPiece(TeamColor.BLACK, PieceType.BISHOP),
                new ChessPiece(TeamColor.BLACK, PieceType.KNIGHT),
                new ChessPiece(TeamColor.BLACK, PieceType.ROOK),
        };

        setBoard(defaultBoard);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        return "ChessBoard" + Arrays.toString(board);
    }
}
