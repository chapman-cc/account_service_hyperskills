package account.services;

import account.dtos.EmployeeDTO;
import account.exceptions.*;
import account.models.Employee;
import account.repositories.EmployeeRepository;
import account.requestBodies.UpdateRoleRequest;
import account.responses.PasswordChangedResponse;
import account.responses.RemoveEmployeeResponse;
import account.responses.SignupResponse;
import account.utils.RoleUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder encoder;
    private final BreachedPasswordService breachedPasswordService;
    private final ModelMapper modelMapper;
    private final RoleUtil  roleUtil;
    @Autowired
    public EmployeeService(EmployeeRepository repo, PasswordEncoder encoder, BreachedPasswordService breachedPasswordService, ModelMapper modaMapper, RoleUtil roleUtil) {
        this.employeeRepository = repo;
        this.encoder = encoder;
        this.breachedPasswordService = breachedPasswordService;
        this.modelMapper = modaMapper;
        this.roleUtil = roleUtil;
    }

    public Optional<Employee> findByEmail(String email) {
        return employeeRepository.findByEmailIgnoreCase(email);
    }


    public boolean validateEmails(List<String> emails) {
        for (String email : emails) {
            boolean exists = employeeRepository.existsByEmailIgnoreCase(email);
            if (!exists) {
                return false;
            }
        }
        return true;
    }

    public SignupResponse register(Employee employee) {
        if (employeeRepository.count() == 0) {
            employee.setRoles(List.of("ADMINISTRATOR"));
        } else {
            employee.setRoles(List.of("USER"));
        }

        if (employeeRepository.existsByEmailIgnoreCase(employee.getEmail())) {
            throw new UserAlreadyExistsException();
        }

        Employee saved = saveAndUpdateEmployee(employee);
        return modelMapper.map(saved, SignupResponse.class);
    }

    public PasswordChangedResponse updatePassword(String email, String password) {
        Employee employee = findByEmail(email).orElseThrow(IllegalArgumentException::new);
        if (encoder.matches(password, employee.getPassword())) {
            throw new PasswordNotChangedException();
        }
        employee.setPassword(password);
        saveAndUpdateEmployee(employee);
        return new PasswordChangedResponse(email);
    }

    @Transactional
    private Employee saveAndUpdateEmployee(Employee employee) {
        String password = employee.getPassword();
        if (breachedPasswordService.check(password)) {
            throw new BreachedPasswordDetectedException();
        }
        String encodedPassword = encoder.encode(password);
        employee.setPassword(encodedPassword);
        return employeeRepository.save(employee);
    }

    public List<EmployeeDTO> getAllEmployee() {
        List<EmployeeDTO> list = new ArrayList<>();
        employeeRepository.findAll().forEach(employee -> {
            EmployeeDTO dto = modelMapper.map(employee, EmployeeDTO.class);
            list.add(dto);
        });
        return list;
    }

    public RemoveEmployeeResponse removeEmployee(String email) {
        Employee employee = employeeRepository.findByEmailIgnoreCase(email)
                .orElseThrow(EmployeeNotFoundException::new);
        if (employee.getRoles().contains("ADMINISTRATOR")) {
            throw new AdminDeletionException();
        }
        employeeRepository.delete(employee);
        return new RemoveEmployeeResponse(employee.getEmail());
    }

    public EmployeeDTO updateRole(UpdateRoleRequest requestBody) {
        final String email = requestBody.getUser();
        final String role = requestBody.getRole();

        if (!roleUtil.isValidRole(role)) {
            throw new RoleNotFoundException();
        }
        Employee employee = employeeRepository.findByEmailIgnoreCase(email)
                .orElseThrow(EmployeeNotFoundException::new);

        return switch (requestBody.getOperation()) {
            case "GRANT"    ->      grantRoleToEmployee(role,employee);
            case "REMOVE"   ->      removeRoleFromEmployee(role, employee);
            default -> throw new IllegalArgumentException();
        };
    }

    private EmployeeDTO grantRoleToEmployee(String role, Employee employee) {
        // cannot combine administrative and business roles
        if (employee.getRoles().contains("ADMINISTRATOR")) {
            throw new RuntimeException("The user cannot combine administrative and business roles!");
        }
        // cannot grant the same role twice
        if (employee.getRoles().contains(role)) {
            throw new RuntimeException("The user does not have a role!");
        }

        employee.getRoles().add(role);
        Employee saved = employeeRepository.save(employee);
        return modelMapper.map(saved, EmployeeDTO.class);
    }
    private EmployeeDTO removeRoleFromEmployee(String role, Employee employee) {
        // cannot remove ADMINISTRATOR role
        if (role.equals("ADMINISTRATOR")) {
            throw new AdminDeletionException();
        }
        // cannot remove the last role
        if (employee.getRoles().size() == 1) {
            throw new RuntimeException("The user must have at least one role!");
        }
        // cannot remove role that does not exist
        if (!employee.getRoles().contains(role)) {
            throw new RuntimeException("The user does not have a role!");
        }
        employee.getRoles().remove(role);
        Employee saved = employeeRepository.save(employee);
        return modelMapper.map(saved, EmployeeDTO.class);
    }
}
