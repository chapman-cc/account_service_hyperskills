package account.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        super("User exist!");
    }
}
