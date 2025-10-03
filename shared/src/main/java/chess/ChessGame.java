package chess;

import chess.ChessPiece.PieceType;

import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor teamTurn;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();

        this.teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null) {
            return null;
        }

        Collection<ChessMove> validMoves = piece.pieceMoves(board, startPosition);

        ChessBoard boardClone = new ChessBoard(board); // copy current state of board

        for (ChessMove potentialMove : validMoves) {
            // Test potential move and remove it if it is invalid
            try {
                makeMove(potentialMove);
            } catch (InvalidMoveException e) {
                validMoves.remove(potentialMove);
            }

            // Reset board to before test move was made
            board = new ChessBoard(boardClone);
        }

        return validMoves;
    }

    /**
     * Makes a move in a chess game.
     * <br><br>
     * If the move is invalid, reverts the board to before the move was made and throws
     * an exception.
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // Make a copy of the board's state
        var boardClone = new ChessBoard(board);

        // Try to make the move
        try {
            tryMove(move);
        } catch (InvalidMoveException e) {
            // If move was invalid, reset the board and pass along the exception
            board = new ChessBoard(boardClone);
            throw e;
        }

    }

    /**
     * Tries to make a move and updates the current team's turn if successful.
     * <br><br>
     * Does not revert the board if move is invalid,
     * but will throw an exception.
     * <br><br>
     * The move is invalid if:
     * <ul>
     *     <li>No piece exists at the starting position.</li>
     *     <li>The piece's team color does not match the current team's color.</li>
     *     <li>board.makeMove() throws an InvalidMoveException.</li>
     *     <li>Making the move puts the current team in check.</li>
     * </ul>
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    private void tryMove(ChessMove move) throws InvalidMoveException {
        // Get start position and piece
        ChessPosition startPos = move.getStartPosition();
        ChessPiece piece = (board.posInBounds(startPos)) ?
                board.getPiece(startPos) :
                null; // null if not in bounds

        // Invalid if no piece exists at the starting position
        if (piece == null) {
            throw new InvalidMoveException(String.format(
                    "No piece located at start position %s", startPos
            ));
        }

        // Invalid if the piece's team color does not match the current team's color
        TeamColor pieceColor = piece.getTeamColor();
        if (pieceColor != teamTurn) {
            throw new InvalidMoveException(String.format(
                    "Attempted to move %s piece %s on %s team's turn",
                    pieceColor, piece, teamTurn
            ));
        }

        // Attempt board.makeMove() (may throw an InvalidMoveException)
        board.makeMove(move);

        // Invalid if making the move puts the current team in check
        if (isInCheck(teamTurn)) {
            throw new InvalidMoveException(String.format(
                    "Attempted a move with %s piece %s that would put their team in check",
                    pieceColor, piece
            ));
        }

        // All checks have passed -> update teamTurn
        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // Get position of king
        ChessPosition kingPos = board.findFirstPiece(PieceType.KING, teamColor);

        // Get all spaces occupied by opposing team color
        TeamColor enemyTeam = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        Collection<ChessPosition> enemyPositions = board.getOccupiedPositions(enemyTeam);

        // Return true if any enemy piece can capture the king
        for (ChessPosition pos : enemyPositions) {
            ChessPiece piece = board.getPiece(pos);
            Collection<ChessMove> pieceMoves = piece.pieceMoves(board, pos);
            for (ChessMove pieceMove : pieceMoves) {
                ChessPosition endPos = pieceMove.getEndPosition();
                if (endPos == kingPos) {
                    return true;
                }
            }
        }

        // No pieces can capture the king -> return false
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // Not in check -> not in checkmate
        if (!isInCheck(teamColor)) {
            return false;
        }

        // Any valid moves -> not in checkmate
        Collection<ChessPosition> teamPositions = board.getOccupiedPositions(teamColor);
        for (ChessPosition pos : teamPositions) {
            if (!validMoves(pos).isEmpty()) {
                return false;
            }
        }

        // No saving cases -> Team is in checkmate
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // In check -> not in stalemate
        if (isInCheck(teamColor)) {
            return false;
        }

        // Any valid moves -> not in stalemate
        Collection<ChessPosition> teamPositions = board.getOccupiedPositions(teamColor);
        for (ChessPosition pos : teamPositions) {
            if (!validMoves(pos).isEmpty()) {
                return false;
            }
        }

        // No saving cases -> Team is in stalemate
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = new ChessBoard(board);
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
