package account.dtos;

import lombok.Builder;

@Builder
public record SignupResponse(long id, String name, String lastname, String email) {
}
