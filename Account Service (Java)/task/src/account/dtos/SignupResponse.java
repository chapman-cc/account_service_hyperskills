package account.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public final class SignupResponse {
    private long id;
    private String name;
    private String lastname;
    private String email;

}
