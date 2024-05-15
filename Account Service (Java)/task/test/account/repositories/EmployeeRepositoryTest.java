package account.repositories;

import account.models.Employee;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class EmployeeRepositoryTest {
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeRepositoryTest(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
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