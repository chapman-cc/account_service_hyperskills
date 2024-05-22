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

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "payrolls")
public class Payroll {
    public static final DateTimeFormatter PERIOD_FORMATTER = DateTimeFormatter.ofPattern("MM-yyyy");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "period")
    private String period;

    @NotNull
    @Min(value = 0, message = "salary cannot be less than 0")
    @Column(name = "salary")
    private Long salary;

    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;


    /**
     * Constructor for Payroll
     *
     * @param period YearMonth
     * @param salary Long
     */
    public Payroll(String period, Long salary) {
        this.period = period;
        this.salary = salary;
    }


    public void setEmployee(Employee employee) {
        this.employee = employee;
        employee.addPayroll(this);
    }
}
