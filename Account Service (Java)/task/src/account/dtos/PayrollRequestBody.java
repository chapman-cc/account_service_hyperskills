package account.dtos;

import account.models.Employee;
import account.utils.Regex;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PayrollRequestBody {
    @NotNull
    @Pattern(regexp = Regex.PAYROLL_PERIOD, message = "incorrect period")
    private String period;

    @NotNull
    @Min(value = 0, message = "salary cannot be less than 0")
    private Long salary;

    @Email(regexp = Regex.EMPLOYEE_EMAIL, message = "incorrect email")
    @JsonProperty("employee")
    @NotNull
    private String employeeEmail;
}
