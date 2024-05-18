package account.utils;


import account.models.Employee;
import account.models.Payroll;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;

import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class EmployeeFaker {
    public static final DateTimeFormatter PAYROLL_PERIOD_FORMATTER = DateTimeFormatter.ofPattern("MM-yyyy");
    private final Faker faker = new Faker();

    public Employee generateEmployee() {
        String name = faker.name().firstName().toLowerCase();
        String lastname = faker.name().lastName().toLowerCase();
        String email = "%s_%s@acme.com".formatted(name, lastname);
        List<String> roles = new ArrayList<>(List.of("USER"));
        return Employee.builder()
                .name(name)
                .lastname(lastname)
                .email(email)
                .password(faker.internet().password(13, 20))
                .roles(roles)
                .build();
    }

    public Employee generateEmployeeWithId() {
        Employee employee = generateEmployee();
        long id = faker.number().numberBetween(1, Integer.MAX_VALUE);
        employee.setId(id);
        return employee;
    }

    public List<Payroll> generatePayrolls(int count) {
        assert count > 0 : "Count must be greater than 0";
        int times = count - 1;

        Payroll initial = generaPayroll();
        List<Payroll> list = new ArrayList<>(List.of(initial));

        for (int i = 0; i < times; i++) {
            Payroll curr = list.get(i);
            Payroll payroll = generatePayrollFromPrev(curr);
            list.add(payroll);
        }
        return list;
    }


    public Payroll generaPayroll() {
        int year10Ago = Year.now().minusYears(10).getValue();
        int yearNow = Year.now().getValue();

        int y = faker.number().numberBetween(year10Ago, yearNow);
        int m = faker.number().numberBetween(1, 12);

        String period = "%d-%d".formatted(m, y);
        if (period.length() != 7) {
            period = "0" + period;
        }

        long salary = faker.number().numberBetween(1000, 9999);
        salary = salary - salary % 100;

        return Payroll.builder()
                .period(period)
                .salary(salary)
                .build();
    }

    public Payroll generatePayrollFromPrev(Payroll payroll) {
        YearMonth period = YearMonth.parse(payroll.getPeriod(), PAYROLL_PERIOD_FORMATTER);
        String newPeriod = period.plusMonths(1).format(PAYROLL_PERIOD_FORMATTER);

        return Payroll.builder()
                .salary(payroll.getSalary())
                .period(newPeriod)
                .build();
    }
}
