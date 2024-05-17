package account.dtos;


import lombok.Builder;

@Builder
public record PasswordChangedResponse(String email, String status) {

    public static final String DEFAULT_MSG = "The password has been updated successfully";

    public PasswordChangedResponse(String email) {
        this(email, DEFAULT_MSG);
    }
}
