package chess;

/**
 * Indicates an invalid move was made in a game
 */
public class InvalidMoveException extends Exception {
    public enum InvalidMoveReason {
        INVALID, GAME_ENDED
    }

    private InvalidMoveReason reason;

    public InvalidMoveException(String message) {
        this(message, InvalidMoveReason.INVALID);
    }

    public InvalidMoveException(String message, InvalidMoveReason reason) {
        super(message);
        this.reason = reason;
    }

    public InvalidMoveReason getReason() {
        return reason;
    }
}
