package service;

public class TeamAlreadyTakenException extends Exception {
    public TeamAlreadyTakenException(String message) {
        super(message);
    }
}
