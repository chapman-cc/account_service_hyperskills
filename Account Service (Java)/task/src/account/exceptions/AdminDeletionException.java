package account.exceptions;

public class AdminDeletionException extends RuntimeException {
    public AdminDeletionException() {
        super("Can't remove ADMINISTRATOR role!");
    }
}
