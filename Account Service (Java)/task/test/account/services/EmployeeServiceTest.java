package account.services;

import account.dtos.EmployeeDTO;
import account.exceptions.EmployeeNotFoundException;
import account.exceptions.RoleNotFoundException;
import account.models.Employee;
import account.repositories.EmployeeRepository;
import account.requestBodies.UpdateRoleRequest;
import account.requestBodies.UserLockOperation;
import account.responses.PasswordChangedResponse;
import account.responses.RemoveEmployeeResponse;
import account.responses.SignupResponse;
import account.responses.UserLockResponse;
import account.utils.EmployeeFaker;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EmployeeFaker faker;

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
                .roles(List.of("USER"))
                .build();

        ArgumentCaptor<Employee> employeeArgumentCaptor = ArgumentCaptor.forClass(Employee.class);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        SignupResponse signupResponse = employeeService.register(employee);
        verify(employeeRepository).save(employeeArgumentCaptor.capture());
        assertThat(employeeArgumentCaptor.getValue()).isEqualTo(employee);

        assertThat(signupResponse.getId()).isEqualTo(employee.getId());
        assertThat(signupResponse.getName()).isEqualTo(employee.getName());
        assertThat(signupResponse.getLastname()).isEqualTo(employee.getLastname());
        assertThat(signupResponse.getEmail()).isEqualTo(employee.getEmail());
        assertThat(signupResponse.getRoles()).isEqualTo(employee.getRoles());
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

    @Test
    void canGetAllEmployees() {
        List<Employee> employees = Stream.generate(faker::generateEmployeeWithId)
                .limit(10)
                .toList();
        when(employeeRepository.findAll()).thenReturn(employees);

        List<EmployeeDTO> all = employeeService.getAllEmployee();

        verify(employeeRepository, times(1)).findAll();

        assertThat(all).hasSize(10);

        assertThat(all).allSatisfy(dto -> {
            assertThat(dto).hasFieldOrProperty("id");
            assertThat(dto).hasFieldOrProperty("name");
            assertThat(dto).hasFieldOrProperty("lastname");
            assertThat(dto).hasFieldOrProperty("email");
            assertThat(dto).hasFieldOrProperty("roles");
            assertThat(dto.getRoles()).isNotEmpty();
        });
    }

    @Test
    void canDeleteEmployeeByEmail() {
        Employee employee = faker.generateEmployee();

        when(employeeRepository.findByEmailIgnoreCase(employee.getEmail())).thenReturn(Optional.of(employee));
        doNothing().when(employeeRepository).delete(employee);

        RemoveEmployeeResponse response = employeeService.removeEmployee(employee.getEmail());

        verify(employeeRepository).delete(employee);

        assertThat(response).isNotNull()
                .hasFieldOrPropertyWithValue("user", employee.getEmail())
                .hasFieldOrPropertyWithValue("status", "Deleted successfully!");
    }

    @Test
    void canUpdateAddRole() {
        Employee employee = faker.generateEmployee();
        String newRole = "ACCOUNTANT";
        UpdateRoleRequest request = UpdateRoleRequest.builder()
                .user(employee.getEmail())
                .role(newRole)
                .operation("GRANT")
                .build();

        when(employeeRepository.findByEmailIgnoreCase(employee.getEmail())).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeDTO dto = employeeService.updateRole(request);

        employee.getRoles().add(newRole);
        verify(employeeRepository).save(employee);

        assertThat(dto).isNotNull()
                .hasFieldOrPropertyWithValue("name", employee.getName())
                .hasFieldOrPropertyWithValue("lastname", employee.getLastname())
                .hasFieldOrPropertyWithValue("email", employee.getEmail())
                .hasFieldOrProperty("roles");
        assertThat(dto.getRoles())
                .isNotEmpty()
                .contains(newRole);
    }

    @Test
    void canLockUser(){
        Employee employee = faker.generateEmployeeWithId();
        employee.getLoginInformation().setLocked(false);
        UserLockOperation operation = UserLockOperation.builder()
                .user(employee.getEmail())
                .operation("LOCK")
                .build();

        when(employeeRepository.findByEmailIgnoreCase(employee.getEmail()))
                .thenReturn(Optional.of(employee));

        UserLockResponse userLockResponse = employeeService.updateUserLockStatus(operation);

        assertThat(userLockResponse).isNotNull()
                .hasFieldOrPropertyWithValue("status", "User %s locked!".formatted(employee.getEmail()));
    }
    @Test
    void cannotLockUserForAdministrator(){
        Employee employee = faker.generateEmployeeWithId();
        employee.getLoginInformation().setLocked(false);
        employee.setRoles(new ArrayList<>(List.of("ADMINISTRATOR")));
        UserLockOperation operation = UserLockOperation.builder()
                .user(employee.getEmail())
                .operation("LOCK")
                .build();

        when(employeeRepository.findByEmailIgnoreCase(employee.getEmail()))
                .thenReturn(Optional.of(employee));

        assertThatThrownBy(() -> employeeService.updateUserLockStatus(operation)).hasMessage("Can't lock the ADMINISTRATOR!");

    }
    @Test
    void canUpdateRemoveRole() {
        Employee employee = faker.generateEmployeeWithId();
        String deleteRole = "USER";
        employee.setRoles(new ArrayList<>(List.of("ACCOUNTANT", deleteRole)));

        when(employeeRepository.findByEmailIgnoreCase(employee.getEmail())).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        UpdateRoleRequest request = new UpdateRoleRequest(employee.getEmail(), deleteRole, "REMOVE");
        EmployeeDTO dto = employeeService.updateRole(request);
        verify(employeeRepository).save(any(Employee.class));

        assertThat(dto).isNotNull()
                .hasFieldOrPropertyWithValue("name", employee.getName())
                .hasFieldOrPropertyWithValue("lastname", employee.getLastname())
                .hasFieldOrPropertyWithValue("email", employee.getEmail())
                .hasFieldOrProperty("roles");
        assertThat(dto.getRoles())
                .isNotEmpty()
                .doesNotContain(deleteRole);
    }


    @Test
    void cannotUpdateIfUserNotFound() {
        Employee employee = faker.generateEmployee();
        UpdateRoleRequest request = UpdateRoleRequest.builder()
                .user(employee.getEmail())
                .role("USER")
                .operation("REMOVE")
                .build();

        when(employeeRepository.findByEmailIgnoreCase(employee.getEmail())).thenReturn(Optional.empty());
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        assertThatThrownBy(() -> employeeService.updateRole(request))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessage("User not found!");
        verify(employeeRepository, times(0)).save(any());
    }

    @Test
    void cannotRemoveRoleIfNotFound() {
        Employee employee = faker.generateEmployee();
        employee.getRoles().addAll(List.of("SOME_ROLE_1", "SOME_ROLE_2"));
        UpdateRoleRequest request = UpdateRoleRequest.builder()
                .user(employee.getEmail())
                .role("MADE_UP_ROLE")
                .operation("REMOVE")
                .build();

        when(employeeRepository.findByEmailIgnoreCase(employee.getEmail())).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        assertThatThrownBy(() -> employeeService.updateRole(request))
                .isInstanceOf(RoleNotFoundException.class)
                .hasMessage("Role not found!");
        verify(employeeRepository, times(0)).save(any());
    }

    @Test
    void cannotRemoveRoleIfUserDoesNotHaveRole() {
        Employee employee = faker.generateEmployee();
        employee.getRoles().add("JANITOR");
        UpdateRoleRequest request = UpdateRoleRequest.builder()
                .user(employee.getEmail())
                .role("ACCOUNTANT")
                .operation("REMOVE")
                .build();

        when(employeeRepository.findByEmailIgnoreCase(employee.getEmail())).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        assertThatThrownBy(() -> employeeService.updateRole(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("The user does not have a role!");
        verify(employeeRepository, times(0)).save(any());
    }

    @Test
    void cannotRemoveRoleIfUserHasOnly1Role() {
        Employee employee = faker.generateEmployee();
        UpdateRoleRequest request = UpdateRoleRequest.builder()
                .user(employee.getEmail())
                .role("USER")
                .operation("REMOVE")
                .build();

        when(employeeRepository.findByEmailIgnoreCase(employee.getEmail())).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        assertThatThrownBy(() -> employeeService.updateRole(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("The user must have at least one role!");
        verify(employeeRepository, times(0)).save(any());
    }

    @Test
    void cannotRemoveAdminRole() {
        Employee employee = faker.generateEmployee();
        final String ADMIN_ROLE = "ADMINISTRATOR";
        employee.setRoles(List.of(ADMIN_ROLE));
        UpdateRoleRequest request = UpdateRoleRequest.builder()
                .user(employee.getEmail())
                .role(ADMIN_ROLE)
                .operation("REMOVE")
                .build();

        when(employeeRepository.findByEmailIgnoreCase(employee.getEmail())).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        assertThatThrownBy(() -> employeeService.updateRole(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Can't remove ADMINISTRATOR role!");
        verify(employeeRepository, times(0)).save(any());
    }

    @Test
    void cannotGrantBusinessRoleToAdmin() {
        Employee employee = faker.generateEmployee();
        final String ADMIN_ROLE = "ADMINISTRATOR";
        employee.setRoles(List.of(ADMIN_ROLE));
        UpdateRoleRequest request = UpdateRoleRequest.builder()
                .user(employee.getEmail())
                .role("USER")
                .operation("GRANT")
                .build();

        when(employeeRepository.findByEmailIgnoreCase(employee.getEmail())).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        assertThatThrownBy(() -> employeeService.updateRole(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("The user cannot combine administrative and business roles!");
        verify(employeeRepository, times(0)).save(any());
    }


}