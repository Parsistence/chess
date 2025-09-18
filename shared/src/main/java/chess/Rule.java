package chess;

import java.util.Collection;

/**
 * The basic rule interface for a chess piece.
 */
public interface Rule {
    /**
     * Get all possible moves a chess piece can make.
     * @param board The chessboard.
     * @param pos The position of the chess piece.
     * @param teamColor The team color the piece belongs to.
     * @return A Collection of all valid moves.
     */
    Collection<ChessMove> getMoves(
            ChessBoard board, ChessPosition pos, ChessGame.TeamColor teamColor
    );
}
