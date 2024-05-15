
package account.repositories;

import account.models.Payroll;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class PayrollRepositoryTest {

    @Autowired
    private PayrollRepository payrollRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        payrollRepository.deleteAll();
    }

    @Test
    void canSavePayroll() {
        Payroll payroll = new Payroll("05-2024", 1000L, "john@acme.com");
        Payroll saved = payrollRepository.save(payroll);
        Assertions.assertThat(saved).isNotNull();
        Assertions.assertThat(saved.getId()).isGreaterThan(0);
    }


    @Test
    void canFindPayrollByEmailAndPeriod() {
        String email = "john@acme.com";
        List<Payroll> payrolls = List.of(
                new Payroll("01-2024", 1000L, email),
                new Payroll("02-2024", 1000L, email),
                new Payroll("03-2024", 1000L, email),
                new Payroll("04-2024", 1000L, email),
                new Payroll("05-2024", 1000L, email)
        );

        payrollRepository.saveAll(payrolls);

        Payroll found = payrollRepository.findByEmployeeAndPeriod(email, "03-2024").orElseThrow();
        Assertions.assertThat(found.getPeriod()).isEqualTo("03-2024");
        Assertions.assertThat(found.getEmployee()).isEqualTo(email);
    }
}