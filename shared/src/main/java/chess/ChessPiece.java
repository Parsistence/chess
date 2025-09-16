package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor teamColor;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor teamColor, ChessPiece.PieceType type) {
        this.teamColor = teamColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos) {
        PieceType pieceType = getPieceType();
        Rule rule = switch (pieceType) {
            case KING -> throw new RuntimeException("Not implemented");
            case QUEEN -> throw new RuntimeException("Not implemented");
            case BISHOP -> new Rule(new int[][]{
                    {1, -1}, {1, 1}, {-1, -1}, {-1, 1}
            });
            case KNIGHT -> throw new RuntimeException("Not implemented");
            case ROOK -> throw new RuntimeException("Not implemented");
            case PAWN -> throw new RuntimeException("Not implemented");
            default -> throw new RuntimeException(String.format(
                    "Rule for piece type %s not implemented.",
                    pieceType
            ));
        };

        return rule.getMoves(board, pos, getTeamColor());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return teamColor == that.teamColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "teamColor=" + teamColor +
                ", type=" + type +
                '}';
    }
}
