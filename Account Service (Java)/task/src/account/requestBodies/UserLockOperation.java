package account.requestBodies;

import account.utils.Regex;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLockOperation {
    @NotEmpty
    @Email(regexp = Regex.EMPLOYEE_EMAIL)
    private String user;

    @Pattern(regexp = "LOCK|UNLOCK")
    private String operation;
}