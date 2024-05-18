package account.repositories;

import account.models.Employee;
import account.models.Payroll;
import account.utils.EmployeeFaker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class EmployeeRepositoryTest {
    private final EmployeeRepository employeeRepository;
    private final PayrollRepository payrollRepository;

    private final EmployeeFaker faker = new EmployeeFaker();

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
        Employee employee = faker.generateEmployee();
        employeeRepository.save(employee);

        assertThat(employeeRepository.existsByEmailIgnoreCase(employee.getEmail())).isTrue();
    }

    @Test
    void shouldNotExistsByEmail() {
        Employee employee = faker.generateEmployee();
        employeeRepository.save(employee);

        assertThat(employeeRepository.existsByEmailIgnoreCase("mary@acme.com")).isFalse();
    }


    @Test
    void shouldFindByEmailIgnoreCase() {
        Employee employee = faker.generateEmployee();
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
        Employee employee1 = faker.generateEmployee();
        Employee employee2 = faker.generateEmployee();

        employee1.setPayrolls(faker.generatePayrolls(3));
        employee2.setPayrolls(faker.generatePayrolls(2));

        employeeRepository.saveAll(List.of(employee1, employee2));

        List<Payroll> payrolls = new ArrayList<>();
        payrollRepository.findAll().forEach(payrolls::add);
        assertThat(payrolls.size()).isEqualTo(5);

        Employee savedEmployee1 = employeeRepository.findById(employee1.getId()).orElseThrow();
        assertThat(savedEmployee1.getPayrolls().size()).isEqualTo(3);

        Employee savedEmployee2 = employeeRepository.findById(employee2.getId()).orElseThrow();
        assertThat(savedEmployee2.getPayrolls().size()).isEqualTo(2);
    }

    @Test
    void canGetAllEmployeeOrderByIdAsc() {
        List<Employee> employees = Stream.generate(faker::generateEmployee)
                .limit(20)
                .toList();

        employeeRepository.saveAll(employees);
        employees = Stream.generate(faker::generateEmployee)
                .limit(50)
                .toList();

        employeeRepository.saveAll(employees);

        List<Employee> savedEmployees = new ArrayList<>();
        employeeRepository.findAll().forEach(savedEmployees::add);

        for (int i = 1; i < savedEmployees.size(); i++) {
            Employee prev = savedEmployees.get(i - 1);
            Employee curr = savedEmployees.get(i);
            assertThat(prev.getId()).isLessThan(curr.getId());
        }
    }
}