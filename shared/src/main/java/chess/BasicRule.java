package chess;

import chess.ChessGame.TeamColor;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public class BasicRule extends AbstractRule {
    private final int[][] potentialMoves;
    private final boolean repeatMoves;

    /**
     * Initialize the rule with each direction the piece can potentially move, assuming
     * the piece can move recursively.
     * @param potentialMoves The potential spaces the piece can move,
     *                       relative to the piece's current position on the board.
     */
    public BasicRule(int[][] potentialMoves) {
        this(potentialMoves, true);
    }

    /**
     * Initialize the rule with each direction the piece can potentially move.
     * @param potentialMoves The potential spaces the piece can move,
     *                       relative to the piece's current position on the board.
     * @param repeatMoves If true, then the piece can move multiple spaces for any given
     *                     direction.
     */
    public BasicRule(int[][] potentialMoves, boolean repeatMoves) {
        this.potentialMoves = potentialMoves;
        this.repeatMoves = repeatMoves;
    }

    @Override
    protected void updateValidMoves(
            Collection<ChessMove> validMoves, ChessBoard board, ChessPosition pos, TeamColor teamColor
    ) {
        for (int[] potentialMove : potentialMoves) {
            for (
                    var newPos = new ChessPosition(
                            pos.getRow() + potentialMove[0],
                            pos.getColumn() + potentialMove[1]
                    );
                    board.posInBounds(newPos);
                    newPos = new ChessPosition(
                            newPos.getRow() + potentialMove[0],
                            newPos.getColumn() + potentialMove[1]
                    )
            ) {
                // Break if space is occupied by a piece of the same color
                ChessPiece thatPiece = board.getPiece(newPos);
                if (thatPiece != null && thatPiece.getTeamColor() == teamColor) {
                    break;
                }

                // Create new ChessMove from new position and add to valid moves
                validMoves.add(new ChessMove(pos, newPos));

                // Don't repeat if this move would capture an enemy
                if (thatPiece != null) {
                    break;
                }

                // Don't repeat if recurseMoves==false
                if (!repeatMoves) {
                    break;
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BasicRule rule = (BasicRule) o;
        return repeatMoves == rule.repeatMoves && Objects.deepEquals(potentialMoves, rule.potentialMoves);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.deepHashCode(potentialMoves), repeatMoves);
    }

    @Override
    public String toString() {
        return "Rule{" +
                "potentialMoves=" + Arrays.toString(potentialMoves) +
                ", recurseMoves=" + repeatMoves +
                '}';
    }
}
