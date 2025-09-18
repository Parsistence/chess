package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * The abstract implementation of Rule.
 */
public abstract class AbstractRule implements Rule {
    /**
     * Get all possible moves a chess piece can make.
     * @param board The chessboard.
     * @param pos The position of the chess piece.
     * @param teamColor The team color the piece belongs to.
     * @return A Collection of all valid moves.
     */
    @Override
    public Collection<ChessMove> getMoves(
            ChessBoard board, ChessPosition pos, ChessGame.TeamColor teamColor
    ) {
        var validMoves = new HashSet<ChessMove>();
        updateValidMoves(validMoves, board, pos, teamColor);
        return validMoves;
    }

    /**
     * Run validation checks for potential moves and store them in validMoves.
     * This is meant to be called from Rule.getMoves, which returns validMoves.
     * @param validMoves The Collection to store all valid moves.
     * @param board The chessboard.
     * @param pos The position of the chess piece.
     * @param teamColor The team color the piece belongs to.
     */
    protected abstract void updateValidMoves(
            Collection<ChessMove> validMoves, ChessBoard board, ChessPosition pos, ChessGame.TeamColor teamColor
    );
}
