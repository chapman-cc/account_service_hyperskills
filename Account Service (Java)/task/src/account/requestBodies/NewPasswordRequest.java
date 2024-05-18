package account.requestBodies;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class NewPasswordRequest {
    @NotEmpty(message = "Password is required!")
    @Length(min = 12, message = "Password length must be 12 chars minimum!")
    @JsonProperty("new_password")
    private String password;
}
