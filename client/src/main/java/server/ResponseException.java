package server;

public class ResponseException extends RuntimeException {
    public ResponseException(String message) {
        super(message);
    }

    public ResponseException(Throwable cause) {
        super(cause);
    }
}
