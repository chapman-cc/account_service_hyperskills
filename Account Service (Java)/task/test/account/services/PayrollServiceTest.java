package account.services;

import account.dtos.PayrollDTO;
import account.dtos.PayrollRequestBody;
import account.exceptions.DuplicateEmployeePeriodException;
import account.models.Employee;
import account.models.Payroll;
import account.repositories.PayrollRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
    @Autowired
    private ModelMapper modelMapper;

    public Employee employee;

    @BeforeEach
    void setUp() {
        employee = Employee.builder().name("John").lastname("Doe").email("johndoe@acme.com").password("secretpassword").build();
    }

    @AfterEach
    void tearDown() {
        payrollRepository.deleteAll();
    }

    @Test
    void canSavePayrolls() {

        Payroll payroll1 = Payroll.builder().period("03-2024").salary(1000L).employee(employee).build();
        Payroll payroll2 = Payroll.builder().period("04-2024").salary(1000L).employee(employee).build();
        Payroll payroll3 = Payroll.builder().period("05-2024").salary(1000L).employee(employee).build();

        List<PayrollRequestBody> bodies = Stream.of(payroll1, payroll2, payroll3)
                .map(payroll -> modelMapper.map(payroll, PayrollRequestBody.class))
                .toList();


        when(employeeService.validateEmails(anyList())).thenReturn(true);
        when(employeeService.findByEmail(anyString())).thenReturn(Optional.of(employee));
        when(payrollRepository.save(any(Payroll.class))).thenReturn(payroll1, payroll2, payroll3);

        List<Payroll> saved = payrollService.savePayrolls(bodies);

        Mockito.verify(employeeService, times(1)).validateEmails(anyList());
        Mockito.verify(payrollRepository, times(1)).saveAll(anyList());

        assertThat(saved).isNotNull();
        assertThat(saved)
                .allSatisfy(payroll ->
                        assertThat(payroll).isNotNull()
                );

    }

    @Test
    void cannotSavePayrollOfSameEmployeeAndPeriod() {
        String period = "05-2024";
        Payroll payroll1 = Payroll.builder().period(period).salary(1000L).employee(employee).build();
        Payroll payroll2 = Payroll.builder().period(period).salary(1500L).employee(employee).build();

        List<PayrollRequestBody> bodies = Stream.of(payroll1, payroll2)
                .map(payroll -> modelMapper.map(payroll, PayrollRequestBody.class))
                .toList();

        when(employeeService.validateEmails(anyList())).thenReturn(true);
        when(employeeService.findByEmail(anyString())).thenReturn(Optional.of(employee));
        when(payrollRepository.save(any(Payroll.class))).thenReturn(payroll1);
        when(payrollRepository.saveAll(anyList())).thenThrow(DataIntegrityViolationException.class);

        Assertions.assertThatThrownBy(() -> payrollService.savePayrolls(bodies))
                .isInstanceOf(DuplicateEmployeePeriodException.class);

    }

    @Test
    void canUpdatePayroll() {
        String period = "05-2024";
        Payroll payroll = Payroll.builder().period(period).salary(1000L).employee(employee).build();
        PayrollRequestBody payrollRequestBody = modelMapper.map(payroll, PayrollRequestBody.class);

        when(employeeService.validateEmails(anyList())).thenReturn(true);
        when(employeeService.findByEmail(eq(payroll.getEmployee().getEmail()))).thenReturn(Optional.of(employee));
        when(payrollRepository.findByEmployeeEmailAndPeriod(eq(employee.getEmail()), eq(period))).thenReturn(Optional.of(payroll));
        when(payrollRepository.save(any(Payroll.class))).thenReturn(payroll);
        when(payrollRepository.saveAll(anyList())).thenReturn(List.of(payroll));

        payrollService.savePayrolls(List.of(payrollRequestBody));

        payroll.setSalary(1500L);
        Payroll updated = payrollService.updatePayroll(payrollRequestBody);

        assertThat(updated).isNotNull();
        assertThat(updated.getPeriod()).isEqualTo(period);
        assertThat(updated.getEmployee().getEmail()).isEqualTo(payroll.getEmployee().getEmail());
    }

//    @Test
//    void cannotUpdatePayrollOfUnknownRecord() {
//        Payroll payroll = Payroll.builder().period("05-2024").salary(1000L).employee(employee).build();
//        PayrollRequestBody payrollRequestBody = PayrollRequestBody.builder()
//                .period(payroll.getPeriod())
//                .salary(payroll.getSalary())
//                .employeeEmail(payroll.getEmployee().getEmail())
//                .build();
//
//        when(employeeService.validateEmails(anyList())).thenReturn(true);
//        when(employeeService.findByEmail(anyString())).thenReturn(Optional.of(employee));
//        when(payrollRepository.findByEmployeeEmailAndPeriod(employee.getEmail(), payroll.getPeriod())).thenReturn(Optional.of(payroll));
//        when(payrollRepository.save(Mockito.any(Payroll.class))).thenReturn(payroll);
//
//        payrollService.savePayrolls(List.of(payrollRequestBody));
//        payroll.setSalary(1500L);
//        Payroll updated = payrollService.updatePayroll(payrollRequestBody);
//
//        assertThat(updated).isNotNull();
//        assertThat(updated.getSalary()).isEqualTo(1500L);
//        assertThat(updated.getPeriod()).isEqualTo(payroll.getPeriod());
//        assertThat(updated.getEmployee().getEmail()).isEqualTo(payroll.getEmployee().getEmail());
//    }

    @Test
    void canGetPayrollsByEmailAndPeriod() {
        Payroll payroll = Payroll.builder().id(1L).period("05-2024").salary(123456L).employee(employee).build();
        PayrollRequestBody.builder().salary(payroll.getSalary()).period(payroll.getPeriod()).employeeEmail(payroll.getEmployee().getEmail()).build();

        when(employeeService.findByEmail(anyString())).thenReturn(Optional.of(employee));
        when(payrollRepository.findByEmployeeEmailAndPeriod(anyString(), anyString())).thenReturn(Optional.of(payroll));

        PayrollDTO payrollDTO = payrollService.getPayroll(payroll.getEmployee().getEmail(), payroll.getPeriod());

        assertThat(payrollDTO).isNotNull();
        assertThat(payrollDTO.getPeriod()).isEqualTo("May-2024");
        assertThat(payrollDTO.getSalary()).isEqualTo("1234 dollar(s) 56 cent(s)");
        assertThat(payrollDTO.getName()).isEqualTo(employee.getName());
        assertThat(payrollDTO.getLastname()).isEqualTo(employee.getLastname());
    }
}