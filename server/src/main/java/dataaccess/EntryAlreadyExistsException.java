package dataaccess;

public class EntryAlreadyExistsException extends DataAccessException {
    public EntryAlreadyExistsException(String message) {
        super(message);
    }

    public EntryAlreadyExistsException(String message, Throwable ex) {
        super(message, ex);
    }

    public EntryAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
