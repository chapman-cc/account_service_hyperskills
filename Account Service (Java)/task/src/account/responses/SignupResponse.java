package account.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public final class SignupResponse {
    private long id;
    private String name;
    private String lastname;
    private String email;
    private List<String> roles;
}
