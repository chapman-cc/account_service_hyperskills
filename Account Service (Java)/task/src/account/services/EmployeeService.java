package account.services;

import account.exceptions.BreachedPasswordDetectedException;
import account.exceptions.PasswordNotChangedException;
import account.models.Employee;
import account.repositories.EmployeeRepository;
import jakarta.transaction.Transactional;
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

    @Autowired
    public EmployeeService(EmployeeRepository repo, PasswordEncoder encoder, BreachedPasswordService breachedPasswordService) {
        this.employeeRepository = repo;
        this.encoder = encoder;
        this.breachedPasswordService = breachedPasswordService;
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

    @Transactional
    public Employee register(Employee employee) {
        String password = employee.getPassword();
        if (breachedPasswordService.check(password)) {
            throw new BreachedPasswordDetectedException();
        }
        String encodedPassword = encoder.encode(password);
        employee.setPassword(encodedPassword);
        if (employee.getRole() == null) {
            employee.setRole("USER");
        }
        return employeeRepository.save(employee);
    }


    public Employee updatePassword(String email, String password) {
        Employee employee = findByEmail(email).orElseThrow(IllegalArgumentException::new);
        if (encoder.matches(password, employee.getPassword())) {
            throw new PasswordNotChangedException();
        }
        employee.setPassword(password);
        return register(employee);
    }
}
