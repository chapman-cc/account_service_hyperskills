package account.repositories;

import account.models.Employee;
import account.models.Payroll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class EmployeeRepositoryTest {
    private final EmployeeRepository employeeRepository;
    private final PayrollRepository payrollRepository;

    @Autowired
    public EmployeeRepositoryTest(EmployeeRepository employeeRepository, PayrollRepository payrollRepository) {
        this.employeeRepository = employeeRepository;
        this.payrollRepository = payrollRepository;
    }

    @AfterEach
    void tearDown() {
        employeeRepository.deleteAll();
    }

    @Test
    void shouldExistsByEmail() {
        Employee employee = getEmployee();
        employeeRepository.save(employee);

        assertThat(employeeRepository.existsByEmailIgnoreCase(employee.getEmail())).isTrue();
    }

    @Test
    void shouldNotExistsByEmail() {
        Employee employee = getEmployee();
        employeeRepository.save(employee);

        assertThat(employeeRepository.existsByEmailIgnoreCase("mary@acme.com")).isFalse();
    }


    @Test
    void shouldFindByEmailIgnoreCase() {
        Employee employee = getEmployee();
        employeeRepository.save(employee);

        String email = employee.getEmail();
        Optional<Employee> optional = employeeRepository.findByEmailIgnoreCase(email);
        Optional<Employee> optional1 = employeeRepository.findByEmailIgnoreCase(email.toUpperCase());

        assertThat(optional.isPresent()).isTrue();
        assertThat(optional.get()).isEqualTo(employee);

        assertThat(optional1.isPresent()).isTrue();
        assertThat(optional1.get()).isEqualTo(employee);
    }

    @Test
    void canSaveEmployeePayrollsCascade() {
        Employee employee1 = getEmployee();
        Employee employee2 = new Employee("Mary", "Moppin", "marymoppin@acme.com", "passwordsecret", "USER");

        employee1.addPayroll(new Payroll("01-2024", 1000L, employee1.getEmail()));
        employee1.addPayroll(new Payroll("02-2024", 1000L, employee1.getEmail()));
        employee1.addPayroll(new Payroll("03-2024", 1000L, employee1.getEmail()));

        employee2.addPayroll(new Payroll("05-2024", 1000L, employee2.getEmail()));
        employee2.addPayroll(new Payroll("06-2024", 1000L, employee2.getEmail()));

        employeeRepository.saveAll(List.of(employee1, employee2));

        List<Payroll> payrolls = new ArrayList<>();
        payrollRepository.findAll().forEach(payrolls::add);
        assertThat(payrolls.size()).isEqualTo(5);

        Employee savedEmployee1 = employeeRepository.findById(employee1.getId()).orElseThrow();
        assertThat(savedEmployee1.getPayrolls().size()).isEqualTo(3);

        Employee savedEmployee2 = employeeRepository.findById(employee2.getId()).orElseThrow();
        assertThat(savedEmployee2.getPayrolls().size()).isEqualTo(2);
    }


    private Employee getEmployee() {
        return new Employee(
                "John",
                "Doe",
                "john@acme.com",
                "password12345",
                "ROLE_ADMIN"
        );
    }
}