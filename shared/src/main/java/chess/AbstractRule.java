package chess;

import java.util.Collection;
import java.util.HashSet;

public abstract class AbstractRule implements Rule {
    @Override
    public Collection<ChessMove> getMoves(
            ChessBoard board, ChessPosition pos, ChessGame.TeamColor teamColor
    ) {
        var validMoves = new HashSet<ChessMove>();
        updateValidMoves(validMoves, board, pos, teamColor);
        return validMoves;
    }

    protected abstract void updateValidMoves(
            Collection<ChessMove> validMoves, ChessBoard board, ChessPosition pos, ChessGame.TeamColor teamColor
    );
}
