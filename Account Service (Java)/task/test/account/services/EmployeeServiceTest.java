package account.services;

import account.models.Employee;
import account.repositories.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class EmployeeServiceTest {
    @MockBean
    private EmployeeRepository employeeRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private BreachedPasswordService breachedPasswordService;

    @Autowired
    private EmployeeService employeeService;

    @Test
    void shouldExistByEmail() {
        Employee employee1 = new Employee("John", "Doe", "john@doe.com", "password123456789", "USER");
        Employee employee2 = new Employee("Mary", "Doe", "mary@doe.com", "password123456789", "USER");

        Mockito
                .when(employeeRepository.existsByEmailIgnoreCase(Mockito.anyString()))
                .thenReturn(true);

        boolean exists = employeeService.validateEmails(List.of(employee1.getEmail(), employee2.getEmail()));
        assertThat(exists).isTrue();
    }

    @Test
    void shouldFindEmployeeByEmail() {
        Employee employee = new Employee("John", "Doe", "john@doe.com", "password123456789", "USER");

        Mockito
                .when(employeeRepository.findByEmailIgnoreCase(employee.getEmail()))
                .thenReturn(Optional.of(employee));

        Optional<Employee> found = employeeService.findByEmail(employee.getEmail());
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get()).isEqualTo(employee);
    }

    @Test
    void canRegisterNewEmployee() {
        Employee employee = new Employee("John", "Doe", "john@doe.com", "password123456789", "USER");

        ArgumentCaptor<Employee> employeeArgumentCaptor = ArgumentCaptor.forClass(Employee.class);

        employeeService.register(employee);

        Mockito.verify(employeeRepository).save(employeeArgumentCaptor.capture());

        Employee capturedEmployee = employeeArgumentCaptor.getValue();

        assertThat(capturedEmployee).isEqualTo(employee);
    }

    @Test
    void canUpdateEmployeePassword() {
        Employee employee = new Employee("John", "Doe", "john@doe.com", "password123456789", "USER");
        employee.setId(1L);

        Mockito.when(employeeRepository.findByEmailIgnoreCase(employee.getEmail())).thenReturn(Optional.of(employee));
        Mockito.when(employeeRepository.save(Mockito.any(Employee.class))).thenReturn(employee);

        Employee updated = employeeService.updatePassword(employee.getEmail(), "new_password");

        Mockito.verify(passwordEncoder).encode(Mockito.anyString());
        Mockito.verify(employeeRepository).save(Mockito.any(Employee.class));
        assertThat(updated).isEqualTo(employee);

    }
}