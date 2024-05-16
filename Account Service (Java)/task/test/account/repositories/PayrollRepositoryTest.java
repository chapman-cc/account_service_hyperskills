
package account.repositories;

import account.models.Employee;
import account.models.Payroll;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class PayrollRepositoryTest {

    @Autowired
    private PayrollRepository payrollRepository;
    @Autowired
    private EmployeeRepository  employeeRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        payrollRepository.deleteAll();
    }


    @Test
    void canFindPayrollByEmailAndPeriod() {
        String email = "john@acme.com";
        List<Payroll> payrolls = List.of(
                new Payroll("01-2024", 1000L, email),
                new Payroll("02-2024", 1000L, email),
                new Payroll("03-2024", 1000L, email),
                new Payroll("04-2024", 1000L, email),
                new Payroll("05-2024", 1000L, email)
        );

        payrollRepository.saveAll(payrolls);

        Payroll found = payrollRepository.findByEmployeeAndPeriod(email, "03-2024").orElseThrow();
        Assertions.assertThat(found.getPeriod()).isEqualTo("03-2024");
        Assertions.assertThat(found.getEmployeeEmail()).isEqualTo(email);
    }

    @Test
    void canFindByEmployee() {
        Employee employee = Employee.builder()
                .name("John")
                .lastname("Doe")
                .email("john@acme.com")
                .password("secretpassword")
                .build();

        Payroll payroll1 = new Payroll("01-2024", 1000L, employee.getEmail());
        Payroll payroll2 = new Payroll("02-2024", 1000L, employee.getEmail());
        employee.addPayrolls(List.of(payroll1, payroll2));

        payrollRepository.saveAll(List.of(payroll1, payroll2));

        employeeRepository.save(employee);

        List<Payroll> found = payrollRepository.findByEmployee(employee);
        Assertions.assertThat(found.size()).isEqualTo(2);
    }
    @Test
    void canFindByEmployeeEmail() {
        Employee employee = Employee.builder()
                .name("John")
                .lastname("Doe")
                .email("john@acme.com")
                .password("secretpassword")
                .build();

        Payroll payroll1 = new Payroll("01-2024", 1000L, employee.getEmail());
        Payroll payroll2 = new Payroll("02-2024", 1000L, employee.getEmail());
        employee.addPayrolls(List.of(payroll1, payroll2));

        payrollRepository.saveAll(List.of(payroll1, payroll2));

        employeeRepository.save(employee);

        List<Payroll> found = payrollRepository.findByEmployeeEmail(employee.getEmail());
        Assertions.assertThat(found.size()).isEqualTo(2);
    }
    @Test
    void canFindByEmployeeEmailAndPeriod() {
        Employee employee = Employee.builder()
                .name("John")
                .lastname("Doe")
                .email("john@acme.com")
                .password("secretpassword")
                .build();

        Payroll payroll1 = new Payroll("01-2024", 1000L, employee.getEmail());
        Payroll payroll2 = new Payroll("02-2024", 1000L, employee.getEmail());

        employee.addPayrolls(List.of(payroll1, payroll2));

        payrollRepository.saveAll(List.of(payroll1, payroll2));

        employeeRepository.save(employee);

        Payroll found = payrollRepository.findByEmployeeEmailAndPeriod(employee.getEmail(), payroll2.getPeriod()).orElseThrow();
        Assertions.assertThat(found.getPeriod()).isEqualTo(payroll2.getPeriod());
    }

}