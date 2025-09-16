package chess;

import java.util.Collection;

public class Rule {
    private final int[][] potentialMoves;
    private final boolean recurseMoves;

    /**
     * Initialize the rule with each direction the piece can potentially move, assuming
     * the piece can move recursively.
     * @param potentialMoves The potential spaces the piece can move,
     *                       relative to the piece's current position on the board.
     */
    public Rule(int[][] potentialMoves) {
        this(potentialMoves, true);
    }

    /**
     * Initialize the rule with each direction the piece can potentially move.
     * @param potentialMoves The potential spaces the piece can move,
     *                       relative to the piece's current position on the board.
     * @param recurseMoves If true, then the piece can move multiple spaces in any given
     *                     direction.
     */
    public Rule(int[][] potentialMoves, boolean recurseMoves) {
        this.potentialMoves = potentialMoves;
        this.recurseMoves = recurseMoves;
    }

    public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition pos) {
        throw new RuntimeException("Not implemented");
    }
}
