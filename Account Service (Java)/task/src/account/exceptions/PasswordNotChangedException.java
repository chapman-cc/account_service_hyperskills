package account.exceptions;

public class PasswordNotChangedException extends RuntimeException {
    public PasswordNotChangedException() {
        super("The passwords must be different!");
    }
}
