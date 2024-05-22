package account.repositories;

import account.models.Employee;
import account.models.Payroll;
import account.utils.EmployeeFaker;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class EmployeeRepositoryTest {
    private final EmployeeRepository employeeRepository;
    private final PayrollRepository payrollRepository;

    private final EntityManager entityManager;
    private final EmployeeFaker faker = new EmployeeFaker();

    @Autowired
    public EmployeeRepositoryTest(EmployeeRepository employeeRepository, PayrollRepository payrollRepository, EntityManager entityManager) {
        this.employeeRepository = employeeRepository;
        this.payrollRepository = payrollRepository;
        this.entityManager = entityManager;
    }

    @AfterEach
    void tearDown() {
        payrollRepository.deleteAll();
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
        Employee employeeLowerCaseRef = employeeRepository.findByEmailIgnoreCase(email).orElseThrow();
        Employee employeeUpperCaseRef = employeeRepository.findByEmailIgnoreCase(email.toUpperCase()).orElseThrow();

        assertThat(employeeLowerCaseRef).isNotNull();
        assertThat(employeeLowerCaseRef).isEqualTo(employee);

        assertThat(employeeUpperCaseRef).isNotNull();
        assertThat(employeeUpperCaseRef).isEqualTo(employee);
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
        List<Payroll> payrolls1 = savedEmployee1.getPayrolls();
        assertThat(payrolls1.size()).isEqualTo(3);

        Employee savedEmployee2 = employeeRepository.findById(employee2.getId()).orElseThrow();
        List<Payroll> payrolls2 = savedEmployee2.getPayrolls();
        assertThat(payrolls2.size()).isEqualTo(2);
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