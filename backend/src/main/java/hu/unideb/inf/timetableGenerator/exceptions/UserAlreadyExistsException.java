package hu.unideb.inf.timetableGenerator.exceptions;

/**
 * Exception thrown when trying to create a user that already exists.
 */
public class UserAlreadyExistsException extends IllegalStateException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
