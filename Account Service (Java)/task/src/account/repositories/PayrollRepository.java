package account.repositories;

import account.models.Payroll;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PayrollRepository extends CrudRepository<Payroll, Long> {

    Optional<Payroll> findByEmployeeAndPeriod(String employee, String period);
    List<Payroll> findByEmployee(String employee);
}
