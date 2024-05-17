package account.services;

import account.dtos.PasswordChangedResponse;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        when(employeeRepository.existsByEmailIgnoreCase(Mockito.anyString()))
                .thenReturn(true);

        boolean exists = employeeService.validateEmails(List.of(employee1.getEmail(), employee2.getEmail()));
        assertThat(exists).isTrue();
    }

    @Test
    void shouldFindEmployeeByEmail() {
        Employee employee = new Employee("John", "Doe", "john@doe.com", "password123456789", "USER");

        when(employeeRepository.findByEmailIgnoreCase(employee.getEmail()))
                .thenReturn(Optional.of(employee));

        Optional<Employee> found = employeeService.findByEmail(employee.getEmail());
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get()).isEqualTo(employee);
    }

    @Test
    void canRegisterNewEmployee() {
        Employee employee = Employee.builder()
                .id(1L)
                .name("John")
                .lastname("Doe")
                .email("john@doe.com")
                .password("password123456789")
                .role("USER")
                .build();

        ArgumentCaptor<Employee> employeeArgumentCaptor = ArgumentCaptor.forClass(Employee.class);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        employeeService.register(employee);

        verify(employeeRepository).save(employeeArgumentCaptor.capture());

        Employee capturedEmployee = employeeArgumentCaptor.getValue();

        assertThat(capturedEmployee).isEqualTo(employee);
    }

    @Test
    void canUpdateEmployeePassword() {
        Employee employee = new Employee("John", "Doe", "john@doe.com", "password123456789", "USER");
        employee.setId(1L);

        when(employeeRepository.findByEmailIgnoreCase(employee.getEmail())).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        PasswordChangedResponse resp = employeeService.updatePassword(employee.getEmail(), "new_password");

        verify(passwordEncoder).encode(Mockito.anyString());
        verify(employeeRepository).save(any(Employee.class));
        assertThat(resp.email()).isEqualTo(employee.getEmail());
        assertThat(resp.status()).isEqualTo(PasswordChangedResponse.DEFAULT_MSG);

    }
}