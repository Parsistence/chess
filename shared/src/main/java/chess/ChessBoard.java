package chess;

import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {}

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
            var item = board[pos.getRow() - 1][pos.getColumn() - 1];
        } catch (ArrayIndexOutOfBoundsException e ) {
            return false;
        }
        return true;
    }

    /**
     * Get the number of rows on the board.
     * @return The number of rows on the board.
     */
    public int numRows() {
        return board.length;
    }

    /**
     * Get the number of columns on the board.
     * @return The number of columns on the board.
     */
    public int numCols() {
        return board[0].length;
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
     * Removes a chess piece from the chessboard
     *
     * @param position where to remove the piece
     */
    public void removePiece(ChessPosition position) {
        try {
            board[position.getRow() - 1][position.getColumn() - 1] = null;
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
     * Get all spaces on the chessboard occupied by a certain team color.
     *
     * @param teamColor The team color to check for.
     * @return A collection of all spaces occupied by the given team color.
     */
    public Collection<ChessPosition> getOccupiedPositions(TeamColor teamColor) {
        Collection<ChessPosition> occupiedSpaces = new HashSet<>();

        for (int r = 1; r <= numRows(); r++) {
            for (int c = 1; c <= numCols(); c++) {
                ChessPosition pos = new ChessPosition(r, c);
                ChessPiece piece = getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    occupiedSpaces.add(pos);
                }
            }
        }

        return occupiedSpaces;
    }

    /**
     * Finds the first piece on the board that matches the given criteria.
     *
     * @param pieceType The type of piece to find.
     * @param teamColor The team color the piece belongs to.
     * @return A ChessPosition of the first piece found; null otherwise
     */
    public ChessPosition findFirstPiece(PieceType pieceType, TeamColor teamColor) {
        // Get all positions that match the team color
        Collection<ChessPosition> teamPositions = getOccupiedPositions(teamColor);

        // Return the first match found
        for (ChessPosition pos : teamPositions) {
            ChessPiece piece = getPiece(pos);
            if (piece.getPieceType() == pieceType) {
                return pos;
            }
        }

        // No piece found -> return null
        return null;
    }

    /**
     * Makes a move on the board according to the given ChessMove if the move is valid.
     * <br><br>
     * The move is invalid if:
     * <ul>
     *     <li>The start or end positions are out of bounds.</li>
     *     <li>No piece exists at the starting position.</li>
     *     <li>The move is not part of the piece's collection of valid piece moves.</li>
     * </ul>
     *
     * @param move The ChessMove to be made.
     * @throws InvalidMoveException if the move is invalid.
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // Invalid if start or end positions are out of bounds
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();
        if (!posInBounds(startPos)) {
            throw new InvalidMoveException(String.format(
                    "Start position of move (%s) is out of bounds", startPos
            ));
        }
        if (!posInBounds(endPos)) {
            throw new InvalidMoveException(String.format(
                    "End position of move (%s) is out of bounds", endPos
            ));
        }

        // Invalid if start position does not contain a piece
        ChessPiece piece = getPiece(startPos);
        if (piece == null) {
            throw new InvalidMoveException(String.format(
                    "No piece located at start position %s", startPos
            ));
        }

        // Invalid if move is not in piece's valid moves
        Collection<ChessMove> pieceMoves = piece.pieceMoves(this, startPos);
        if (!pieceMoves.contains(move)) {
            throw new InvalidMoveException(String.format(
                    "The given chess move (%s) is not a valid move for piece %s",
                    move, piece
            ));
        }

        // All checks passed -> make the move
        addPiece(endPos, piece);
        removePiece(startPos);
    }

    /**
     * Sets the board to the values in newBoard.
     *
     * @param newBoard the new board to copy into board.
     */
    public void setBoard(ChessPiece[][] newBoard) {
        System.arraycopy(newBoard, 0, board, 0, board.length);
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
