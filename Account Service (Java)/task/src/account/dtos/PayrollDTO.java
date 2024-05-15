package account.dtos;

import account.utils.Regex;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
public class PayrollDTO {
    private String name;
    private String lastname;
    private String period;
    private String salary;
}
