package account.services;

import account.dtos.PayrollDTO;
import account.exceptions.DuplicateEmployeePeriodException;
import account.models.Employee;
import account.models.Payroll;
import account.repositories.PayrollRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class PayrollServiceTest {

    @MockBean
    private PayrollRepository payrollRepository;
    @MockBean
    private EmployeeService employeeService;
    @Autowired
    private PayrollService payrollService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void canSavePayrolls() {
        List<Payroll> payrolls = List.of(
                Payroll.builder().period("05-2024").salary(1000L).employee("john@acme.com").build()
        );

        when(employeeService.validateEmails(anyList())).thenReturn(true);
        when(payrollRepository.save(any(Payroll.class))).thenReturn(Payroll.builder().build());

        List<Payroll> saved = payrollService.savePayrolls(payrolls);

        Mockito.verify(employeeService, times(payrolls.size())).validateEmails(anyList());
        Mockito.verify(payrollRepository, times(payrolls.size())).saveAll(anyList());

        assertThat(saved).isNotNull();
        assertThat(saved)
                .allSatisfy(payroll ->
                        assertThat(payroll).isNotNull()
                );

    }

    @Test
    void cannotSavePayrollOfSameEmployeeAndPeriod() {
        String mail = "john@acme.com";
        String period = "05-2024";
        Payroll payroll1 = Payroll.builder().period(period).salary(1000L).employee(mail).build();
        Payroll payroll2 = Payroll.builder().period(period).salary(1500L).employee(mail).build();


        when(employeeService.validateEmails(anyList())).thenReturn(true);
        when(payrollRepository.save(any(Payroll.class))).thenReturn(Payroll.builder().build());
        when(payrollRepository.saveAll(anyList())).thenThrow(DataIntegrityViolationException.class);

        Assertions.assertThatThrownBy(() -> payrollService.savePayrolls(List.of(payroll1, payroll2)))
                .isInstanceOf(DuplicateEmployeePeriodException.class);

        Assertions.assertThatThrownBy(() -> payrollService.savePayrolls(List.of(payroll1)))
                .isInstanceOf(DataIntegrityViolationException.class);

    }

    @Test
    void canUpdatePayroll() {
        String email = "john@acme.com";
        String period = "05-2024";
        Payroll payroll = new Payroll(period, 1000L, email);
        Employee employee = new Employee("John", "Doe", payroll.getEmployee(), "secretpassword", "USER");

        when(employeeService.findByEmail(eq(email))).thenReturn(Optional.of(employee));
        when(employeeService.validateEmails(anyList())).thenReturn(true);
        when(payrollRepository.findByEmployeeAndPeriod(eq(email), eq(period))).thenReturn(Optional.of(payroll));
        when(payrollRepository.save(any(Payroll.class))).thenReturn(payroll);
        when(payrollRepository.saveAll(anyList())).thenReturn(List.of(payroll));

        payrollService.savePayrolls(List.of(payroll));
        payroll.setSalary(1500L);
        Payroll updated = payrollService.updatePayroll(payroll);

        assertThat(updated).isNotNull();
        assertThat(updated.getSalary()).isEqualTo(1500L);
        assertThat(updated.getPeriod()).isEqualTo(period);
        assertThat(updated.getEmployee()).isEqualTo(email);
    }

    @Test
    void cannotUpdatePayrollOfUnknownRecord() {
        Payroll payroll = new Payroll("05-2024", 1000L, "john@acme.com");
        Employee employee = new Employee();
        employee.setEmail(payroll.getEmployee());

        when(employeeService.findByEmail(anyString())).thenReturn(Optional.of(employee));
        when(employeeService.validateEmails(anyList())).thenReturn(true);
        when(payrollRepository.findByEmployeeAndPeriod(payroll.getEmployee(), payroll.getPeriod())).thenReturn(Optional.of(payroll));
        when(payrollRepository.save(Mockito.any(Payroll.class))).thenReturn(payroll);

        payrollService.savePayrolls(List.of(payroll));
        payroll.setSalary(1500L);
        Payroll updated = payrollService.updatePayroll(payroll);

        assertThat(updated).isNotNull();
        assertThat(updated.getSalary()).isEqualTo(1500L);
        assertThat(updated.getPeriod()).isEqualTo(payroll.getPeriod());
        assertThat(updated.getEmployee()).isEqualTo(payroll.getEmployee());
    }

    @Test
    void canGetPayrollsByEmailAndPeriod() {
        String email = "johndoe@acme.com";
        String period = "05-2024";
        long salary = 123456L;

        Employee employee = new Employee("John", "Doe", email, "secretpassword", "USER");
        Payroll payroll = Payroll.builder().id(1L).period(period).salary(salary).employee(email).build();

        when(employeeService.findByEmail(anyString())).thenReturn(Optional.of(employee));
        when(payrollRepository.findByEmployeeAndPeriod(anyString(), anyString())).thenReturn(Optional.of(payroll));

        PayrollDTO payrollDTO = payrollService.getPayroll(email, period);

        assertThat(payrollDTO).isNotNull();
        assertThat(payrollDTO.getPeriod()).isEqualTo("May-2024");
        assertThat(payrollDTO.getSalary()).isEqualTo("1234 dollar(s) 56 cent(s)");
        assertThat(payrollDTO.getName()).isEqualTo(employee.getName());
        assertThat(payrollDTO.getLastname()).isEqualTo(employee.getLastname());
    }
}