package account.services;

import account.dtos.PasswordChangedResponse;
import account.dtos.SignupResponse;
import account.exceptions.BreachedPasswordDetectedException;
import account.exceptions.PasswordNotChangedException;
import account.exceptions.UserAlreadyExistsException;
import account.models.Employee;
import account.repositories.EmployeeRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder encoder;
    private final BreachedPasswordService breachedPasswordService;
    private final ModelMapper modelMapper;

    @Autowired
    public EmployeeService(EmployeeRepository repo, PasswordEncoder encoder, BreachedPasswordService breachedPasswordService, ModelMapper modaMapper) {
        this.employeeRepository = repo;
        this.encoder = encoder;
        this.breachedPasswordService = breachedPasswordService;
        this.modelMapper = modaMapper;
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
        if (employeeRepository.existsByEmailIgnoreCase(employee.getEmail())) {
            throw new UserAlreadyExistsException();
        }
        if (employee.getRole() == null) {
            employee.setRole("USER");
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
}
