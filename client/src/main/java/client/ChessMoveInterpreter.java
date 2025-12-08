package client;

import chess.ChessPiece.PieceType;
import chess.ChessPosition;
import org.junit.jupiter.api.Assertions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class ChessMoveInterpreter {
    private final Map<Character, PieceType> promotionMap = Map.of(
            'q', PieceType.QUEEN,
            'r', PieceType.ROOK,
            'b', PieceType.BISHOP,
            'k', PieceType.KNIGHT
    );

    public ChessPosition positionFromString(String posString) {
        if (!validatePositionString(posString)) {
            throw new IllegalArgumentException("Chess move string should be of the format `A1` or `a1`");
        }

        char colChar = Character.toLowerCase(posString.charAt(0));
        int col = colChar - 'a' + 1;
        int row = posString.charAt(1) - '0';
        return new ChessPosition(row, col);
    }

    public PieceType promotionTypeFromString(String pieceString) {
        PieceType result;
        try {
            var pieceChar = Character.toLowerCase(pieceString.charAt(0));
            result = promotionMap.get(pieceChar);
            Assertions.assertNotNull(result);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Input should be a char representing a valid promotion piece (" + promotionMapDetails() + ")"
            );
        }
        return result;
    }

    private String promotionMapDetails() {
        Collection<String> promotions = new HashSet<>();
        promotionMap.forEach((c, piece) -> promotions.add("'" + c + "' - " + piece));
        return String.join(", ", promotions);
    }

    public boolean validatePositionString(String posString) {
        if (posString.length() < 2) {
            return false;
        }
        if (!Character.isLetter(posString.charAt(0))) {
            return false;
        }

        return Character.isDigit(posString.charAt(1));
    }
}
