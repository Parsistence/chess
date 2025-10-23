package dataaccess;

public class EntryAlreadyExistsException extends DataAccessException {
    public EntryAlreadyExistsException(String message) {
        super(message);
    }
}
