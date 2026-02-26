package hu.unideb.inf.timetableGenerator.exceptions;

public class UserAlreadyExistsException extends IllegalStateException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
