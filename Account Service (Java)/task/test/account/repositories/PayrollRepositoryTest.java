
package account.repositories;

import account.models.Employee;
import account.models.Payroll;
import account.utils.EmployeeFaker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PayrollRepositoryTest {

    private final PayrollRepository payrollRepository;

    private final EmployeeRepository employeeRepository;
    private final LoginInformationRepository loginInformationRepository;

    private final EmployeeFaker faker = new EmployeeFaker();

    @Autowired
    public PayrollRepositoryTest(PayrollRepository payrollRepository, EmployeeRepository employeeRepository, LoginInformationRepository loginInformationRepository) {
        this.payrollRepository = payrollRepository;
        this.employeeRepository = employeeRepository;
        this.loginInformationRepository = loginInformationRepository;
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        loginInformationRepository.deleteAll();
        payrollRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    @Test
    void willSavePayrollsByEmployeeCascade() {
        Employee employee = faker.generateEmployee();
        List<Payroll> payrolls = faker.generatePayrolls(5);

        employee.setPayrolls(payrolls);

        employeeRepository.save(employee);

        Iterable<Payroll> all = payrollRepository.findAll();
        assertThat(all).hasSize(5);
    }

    @Test
    void saveAllWillEntityWillHaveId() {
        Employee employee = faker.generateEmployee();
        List<Payroll> payrolls = faker.generatePayrolls(5);

        employee.setPayrolls(payrolls);

        employeeRepository.save(employee);

        assertThat(payrolls).allSatisfy(payroll ->
                assertThat(payroll.getId())
                        .isNotNull()
                        .isGreaterThan(0)
        );
    }

    @Test
    void canFindByEmployeeEmail() {
        Employee employee = faker.generateEmployee();
        employeeRepository.save(employee);

        List<Payroll> payrolls = faker.generatePayrolls(5);
        payrolls.forEach(payroll -> payroll.setEmployee(employee));

        payrollRepository.saveAll(payrolls);

        List<Payroll> found = payrollRepository.findByEmployeeEmail(employee.getEmail());
        assertThat(found.size()).isEqualTo(5);
    }

    @Test
    void canFindByEmployee() {
        Employee employee = faker.generateEmployee();

        employeeRepository.save(employee);

        List<Payroll> payrolls = faker.generatePayrolls(5);
        payrolls.forEach(payroll -> payroll.setEmployee(employee));

        payrollRepository.saveAll(payrolls);

        List<Payroll> found = payrollRepository.findByEmployee(employee);
        assertThat(found.size()).isEqualTo(5);
    }

    @Test
    void canFindPayrollByEmployeeAndPeriod() {
        Employee employee = faker.generateEmployee();

        employeeRepository.save(employee);

        List<Payroll> payrolls = faker.generatePayrolls(5);
        payrolls.forEach(payroll -> payroll.setEmployee(employee));

        payrollRepository.saveAll(payrolls);

        Payroll selected = payrolls.get(3);

        Optional<Payroll> found = payrollRepository.findByEmployeeAndPeriod(employee, selected.getPeriod());
        assertThat(found).isPresent();
        assertThat(found.get().getPeriod()).isEqualTo(selected.getPeriod());
        assertThat(found.get().getSalary()).isEqualTo(selected.getSalary());
    }


    @Test
    void canFindByEmployeeEmailAndPeriod() {
        Employee employee = faker.generateEmployee();

        employeeRepository.save(employee);

        List<Payroll> payrolls = faker.generatePayrolls(5);
        payrolls.forEach(payroll -> payroll.setEmployee(employee));

        payrollRepository.saveAll(payrolls);

        Payroll selected = payrolls.get(3);

        Optional<Payroll> found = payrollRepository.findByEmployeeEmailAndPeriod(employee.getEmail(), selected.getPeriod());
        assertThat(found).isPresent();
        assertThat(found.get().getPeriod()).isEqualTo(selected.getPeriod());
        assertThat(found.get().getSalary()).isEqualTo(selected.getSalary());
        assertThat(found.get().getEmployee().getEmail()).isEqualTo(employee.getEmail());
    }

    @Test
    void canUpdatePayroll() {
        Employee employee = faker.generateEmployee();

        employeeRepository.save(employee);

        Payroll payroll = faker.generaPayroll();
        payroll.setEmployee(employee);
        Long salary = payroll.getSalary();

        payrollRepository.save(payroll);

        Payroll found = payrollRepository.findById(payroll.getId()).orElseThrow();

        found.setSalary(salary + 1000L);

        payrollRepository.save(found);

        Payroll found2 = payrollRepository.findById(payroll.getId()).orElseThrow();

        assertThat(found2.getSalary()).isEqualTo(salary + 1000L);
    }
}