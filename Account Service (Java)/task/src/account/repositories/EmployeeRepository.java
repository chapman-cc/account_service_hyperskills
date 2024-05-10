package account.repositories;

import account.models.Employee;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {
    boolean existsByEmail(String email);

    Optional<Employee> findByEmailIgnoreCase(String email);
}
