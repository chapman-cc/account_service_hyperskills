package account.services;

import account.models.Employee;
import account.repositories.EmployeeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeService {
    private final EmployeeRepository repo;
    private final PasswordEncoder encoder;

    @Autowired
    public EmployeeService(EmployeeRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public Optional<Employee> findByEmail(String email) {
        return repo.findByEmailIgnoreCase(email);
    }

    @Transactional
    public Employee register(Employee employee) {
        String password = employee.getPassword();
        String encoded = encoder.encode(password);
        employee.setPassword(encoded);
        if (employee.getRole() == null) {
            employee.setRole("USER");
        }
        return repo.save(employee);
    }

}
