
package account.repositories;

import account.models.Employee;
import account.models.Payroll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class PayrollRepositoryTest {

    @Autowired
    private PayrollRepository payrollRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .name("John")
                .lastname("Doe")
                .email("john@acme.com")
                .password("secretpassword")
                .build();
    }

    @AfterEach
    void tearDown() {
        payrollRepository.deleteAll();
        employeeRepository.deleteAll();
    }


    @Test
    void canFindPayrollByEmployeeAndPeriod() {
        Employee employee = Employee.builder()
                .name("John")
                .lastname("Doe")
                .email("john@acme.com")
                .password("secretpassword")
                .build();
        List<Payroll> payrolls = List.of(
                new Payroll("01-2024", 1000L),
                new Payroll("02-2024", 1000L),
                new Payroll("03-2024", 1000L),
                new Payroll("04-2024", 1000L),
                new Payroll("05-2024", 1000L)
        );

        payrolls.forEach(payroll -> payroll.setEmployee(employee));

        payrollRepository.saveAll(payrolls);

        String email = employee.getEmail();
        String period = payrolls.get(2).getPeriod();
        Payroll found = payrollRepository.findByEmployeeAndPeriod(employee, period).orElseThrow();
        assertThat(found.getPeriod()).isEqualTo(period);
        assertThat(found.getEmployee().getEmail()).isEqualTo(email);
    }

    @Test
    void canFindByEmployee() {
        Payroll payroll1 = new Payroll("01-2024", 1000L);
        Payroll payroll2 = new Payroll("02-2024", 1000L);

        List<Payroll> payrolls = List.of(payroll1, payroll2);
        payrolls.forEach(payroll -> payroll.setEmployee(employee));

        payrollRepository.saveAll(payrolls);

        List<Payroll> found = payrollRepository.findByEmployee(employee);
        assertThat(found.size()).isEqualTo(2);
    }

    @Test
    void canFindByEmployeeEmail() {
        Payroll payroll1 = new Payroll("01-2024", 1000L);
        Payroll payroll2 = new Payroll("02-2024", 1000L);
        List<Payroll> payrolls = List.of(payroll1, payroll2);

        payrolls.forEach(payroll -> payroll.setEmployee(employee));

        payrollRepository.saveAll(payrolls);

        List<Payroll> found = payrollRepository.findByEmployeeEmail(employee.getEmail());
        assertThat(found.size()).isEqualTo(2);
    }

    @Test
    void canFindByEmployeeAndPeriod() {
        Payroll payroll1 = new Payroll("01-2024", 1000L);
        Payroll payroll2 = new Payroll("02-2024", 1000L);

        employee.addPayrolls(List.of(payroll1, payroll2));

        payrollRepository.saveAll(List.of(payroll1, payroll2));

        Payroll found = payrollRepository.findByEmployeeAndPeriod(employee, payroll2.getPeriod()).orElseThrow();
        assertThat(found.getPeriod()).isEqualTo(payroll2.getPeriod());
    }

    @Test
    void canFindByEmployeeEmailAndPeriod() {
        Payroll payroll1 = new Payroll("01-2024", 1000L);
        Payroll payroll2 = new Payroll("02-2024", 1000L);

        employee.addPayrolls(List.of(payroll1, payroll2));

        payrollRepository.saveAll(List.of(payroll1, payroll2));

        Payroll found = payrollRepository.findByEmployeeEmailAndPeriod(employee.getEmail(), payroll2.getPeriod()).orElseThrow();
        assertThat(found.getPeriod()).isEqualTo(payroll2.getPeriod());
    }

    @Test
    void canUpdatePayroll() {
        Payroll payroll1 = Payroll.builder().salary(1000L).period("01-2024").employee(employee).build();
        Payroll payroll2 = Payroll.builder().salary(1000L).period("02-2024").employee(employee).build();
        Payroll payroll3 = Payroll.builder().salary(1000L).period("03-2024").employee(employee).build();

        payrollRepository.saveAll(List.of(payroll1, payroll2, payroll3));

        Payroll toBeUpdated = payrollRepository.findByEmployeeEmailAndPeriod(employee.getEmail(), "02-2024").orElseThrow();
        toBeUpdated.setSalary(1200L);

        payrollRepository.save(toBeUpdated);

        List<Payroll> list = new ArrayList<>();
        payrollRepository.findAll().forEach(list::add);

        assertThat(list.size()).isEqualTo(3);
        assertThat(list.stream().filter(p -> p.getSalary() > 1000L).count()).isEqualTo(1);
    }

    @Test
    void saveAllReturnsPayrolls() {
        Payroll payroll1 = Payroll.builder().salary(1000L).period("01-2024").employee(employee).build();
        Payroll payroll2 = Payroll.builder().salary(1000L).period("02-2024").employee(employee).build();
        Payroll payroll3 = Payroll.builder().salary(1000L).period("03-2024").employee(employee).build();

        List<Payroll> payrolls = List.of(payroll1, payroll2, payroll3);

        List<Payroll> target = new ArrayList<>();

        payrollRepository.saveAll(payrolls).forEach(target::add);

        assertThat(target.size()).isEqualTo(3);
        assertThat(target).allSatisfy(payroll -> assertThat(payroll.getId()).isGreaterThan(0))
                .allSatisfy(payroll -> assertThat(payroll.getEmployee().getId()).isEqualTo(employee.getId()))
                .allSatisfy(payroll -> assertThat(payroll.getSalary()).isEqualTo(1000L));
    }
}