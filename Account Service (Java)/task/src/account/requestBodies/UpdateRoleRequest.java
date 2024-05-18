package account.requestBodies;

import account.utils.Regex;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UpdateRoleRequest {
    @NotEmpty(message = "User cannot be empty")
    @Email(regexp = Regex.EMPLOYEE_EMAIL, message = "Invalid email")
    private String user;

    @NotEmpty(message = "Role cannot be empty")
    private String role;

    @Pattern(regexp = "GRANT|REMOVE", message = "Operation must be GRANT or REMOVE")
    private String operation;
}
