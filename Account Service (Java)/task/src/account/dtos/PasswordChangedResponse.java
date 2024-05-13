package account.dtos;


public record PasswordChangedResponse(String email, String status) {

    public PasswordChangedResponse(String email) {
        this(email, "The password has been updated successfully");
    }
}
