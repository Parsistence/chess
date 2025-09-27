package chess;

import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;

public class PawnRule implements Rule {
    private final PieceType[] promotionPieces = {
            PieceType.QUEEN, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK
    };

    /**
     * Checks all potential moves a pawn can make and adds all the valid moves
     * to validMoves, a collection used by AbstractRule.getMoves().
     * Checks for the following cases:
     * <ul>
     *     <li><b>Forward one space</b> (space must be free)</li>
     *     <li><b>Forward two spaces</b> (both spaces must be free)</li>
     *     <li><b>Forward diagonal</b> (must be capturing an enemy piece)</li>
     *     <li><b>Pawn promotion</b> (tested in all cases above)</li>
     * <ul/>
     * @param board The chessboard.
     * @param pos The current position of the pawn.
     * @param teamColor The team color the pawn belongs to.
     */
    @Override
    public Collection<ChessMove> getMoves(
            ChessBoard board, ChessPosition pos, TeamColor teamColor
    ) {
        var validMoves = new HashSet<ChessMove>();

        // Get promotion row from team color
        int promotionRow = (teamColor == TeamColor.WHITE) ? board.numRows() : 1;

        // Get move direction from team color
        int forwardDir = (teamColor == TeamColor.WHITE) ? 1 : -1;

        // Lambda used to add all piece promotion possibilities when updating validMoves
        Consumer<ChessPosition> tryPromotion = (newPos) -> {
            if (newPos.getRow() == promotionRow) {
                for (PieceType promotionPiece : promotionPieces) {
                    validMoves.add(new ChessMove(pos, newPos, promotionPiece));
                }
            } else {
                validMoves.add(new ChessMove(pos, newPos));
            }
        };

        // Check straight ahead
        var forwardPos = new ChessPosition(pos.getRow() + forwardDir, pos.getColumn());
        if (board.posInBounds(forwardPos) && board.getPiece(forwardPos) == null) {
            tryPromotion.accept(forwardPos);

            // If on first move, check two spaces ahead
            if (
                    pos.getRow() == ((teamColor == TeamColor.WHITE) ?
                            2 :
                            board.numRows() - 1)
            ) {
                var doubleForward = new ChessPosition(pos.getRow() + 2 * forwardDir, pos.getColumn());
                if (board.posInBounds(doubleForward) && board.getPiece(doubleForward) == null) {
                    tryPromotion.accept(doubleForward);
                }
            }
        }

        // Check capture diagonal spaces
        int[] diagonalDirs = {-1, 1};
        for (int diagonalDir : diagonalDirs) {
            var diagonalPos = new ChessPosition(
                    pos.getRow() + forwardDir, pos.getColumn() + diagonalDir
            );

            if (board.posInBounds(diagonalPos) &&
                    board.getPiece(diagonalPos) != null &&
                    board.getPiece(diagonalPos).getTeamColor() != teamColor
            ) {
                tryPromotion.accept(diagonalPos);
            }
        }

        return validMoves;
    }

    @Override
    public String toString() {
        return "PawnRule{}";
    }
}
