package account.exceptions;

public class BreachedPasswordDetectedException extends RuntimeException {
    public BreachedPasswordDetectedException() {
        super("The password is in the hacker's database!");
    }
}
