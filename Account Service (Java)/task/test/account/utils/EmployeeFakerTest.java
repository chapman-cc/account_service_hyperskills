package account.utils;

import account.models.Employee;
import account.models.Payroll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class EmployeeFakerTest {
    @Autowired
    private EmployeeFaker faker;

    @Test
    void canCreateFakeEmployee() {
        Employee employee = faker.generateEmployee();

        assertThat(employee).hasFieldOrProperty("name");
        assertThat(employee).hasFieldOrProperty("lastname");
        assertThat(employee).hasFieldOrProperty("email");
        assertThat(employee).hasFieldOrProperty("password");
        assertThat(employee).hasFieldOrProperty("roles");
        assertThat(employee.getRoles()).asList().isNotEmpty();
    }

    @Test
    void canCreateFakeEmployeeWithId() {
        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            employees.add(faker.generateEmployeeWithId());

        }

        assertThat(employees).allSatisfy(employee -> {
            assertThat(employee).hasFieldOrProperty("id");
            assertThat(employee.getId()).isGreaterThan(0);
        });

    }

    @Test
    void canGeneratePayrolls() {
        int count = 10;
        DateTimeFormatter fmt = Payroll.PERIOD_FORMATTER;
        List<Payroll> payrolls = faker.generatePayrolls(count);

        assertThat(payrolls).hasSize(count);

        assertThat(payrolls).allSatisfy(payroll -> {
            assertThat(payroll).hasFieldOrProperty("period");
            assertThat(payroll).hasFieldOrProperty("salary");
            assertThat(payroll.getPeriod()).matches(Regex.PAYROLL_PERIOD);
        });

        Set<String> periodSet = payrolls.stream().map(Payroll::getPeriod).collect(Collectors.toSet());
        assertThat(periodSet).hasSize(count);

        for (int i = 1; i < payrolls.size(); i++) {
            String prev = payrolls.get(i - 1).getPeriod();
            String curr = payrolls.get(i).getPeriod();

            YearMonth prevYM = YearMonth.parse(prev, fmt);
            YearMonth currYM = YearMonth.parse(curr, fmt);

            assertThat(prevYM.isBefore(currYM)).isTrue();
        }

    }
}
