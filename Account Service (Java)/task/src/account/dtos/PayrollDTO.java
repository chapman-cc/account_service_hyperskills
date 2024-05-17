package account.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayrollDTO {
    private String name;
    private String lastname;
    private String period;
    private String salary;
}
