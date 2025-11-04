package dataaccess;

public class EntryNotFoundException extends DataAccessException {
    public EntryNotFoundException(String message) {
        super(message);
    }

    public EntryNotFoundException(String message, Throwable ex) {
        super(message, ex);
    }

    public EntryNotFoundException(Throwable cause) {
        super(cause);
    }
}
