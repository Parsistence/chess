package chess;

import chess.ChessGame.TeamColor;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

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

    public Collection<ChessMove> getMoves(
            ChessBoard board, ChessPosition pos, TeamColor teamColor
    ) {
        var validMoves = new HashSet<ChessMove>();
        updateValidMoves(validMoves, board, pos, teamColor);
        return validMoves;
    }

    private void updateValidMoves(
            Collection<ChessMove> validMoves, ChessBoard board, ChessPosition pos, TeamColor teamColor
    ) {
        for (int[] potentialMove : potentialMoves) {
            int row = pos.getRow() + potentialMove[0];
            int col = pos.getColumn() + potentialMove[1];
            var newPos = new ChessPosition(row, col);

            // Return if off the board
            if (!board.posInBounds(newPos)) {
                return;
            }

            // Return if space is occupied by a piece of the same color
            ChessPiece thatPiece = board.getPiece(newPos);
            if (thatPiece != null && thatPiece.getTeamColor() == teamColor) {
                return;
            }

            // Create new ChessMove from new position and add to valid moves
            validMoves.add(new ChessMove(pos, newPos));

            // Recurse with new position if recurseMoves==true
            if (recurseMoves) {
                updateValidMoves(validMoves, board, newPos, teamColor);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Rule rule = (Rule) o;
        return recurseMoves == rule.recurseMoves && Objects.deepEquals(potentialMoves, rule.potentialMoves);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.deepHashCode(potentialMoves), recurseMoves);
    }

    @Override
    public String toString() {
        return "Rule{" +
                "potentialMoves=" + Arrays.toString(potentialMoves) +
                ", recurseMoves=" + recurseMoves +
                '}';
    }
}
