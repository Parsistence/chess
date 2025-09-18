package chess;

import java.util.Collection;

public interface Rule {
    Collection<ChessMove> getMoves(
            ChessBoard board, ChessPosition pos, ChessGame.TeamColor teamColor
    );
}
