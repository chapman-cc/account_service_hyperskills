package account.models;

import account.utils.Regex;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "payrolls")
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "period")
    @Pattern(regexp = Regex.PAYROLL_PERIOD, message = "incorrect period")
    private String period;

    @NotNull
    @Min(value = 0, message = "salary cannot be less than 0")
    @Column(name = "salary")
    private Long salary;

//    @Email(regexp = Regex.EMPLOYEE_EMAIL, message = "incorrect email")
//    @NotNull
//    @Column(name = "employee_email")
//    private String employeeEmail;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "employee_id")
    private Employee employee;

    /**
     * Constructor for Payroll
     *
     * @param period
     * @param salary
     */
    public Payroll(String period, Long salary) {
        this.period = period;
        this.salary = salary;
    }
}
