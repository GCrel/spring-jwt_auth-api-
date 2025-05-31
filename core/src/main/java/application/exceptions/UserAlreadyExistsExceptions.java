package application.exceptions;

public class UserAlreadyExistsExceptions extends IllegalArgumentException {
    public UserAlreadyExistsExceptions(String message) {
        super(message);
    }
}
