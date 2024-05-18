package account.utils;

import account.dtos.PayrollDTO;
import account.requestBodies.PayrollRequest;
import account.responses.SignupResponse;
import account.models.Employee;
import account.models.Payroll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ModelMapperTest {

    @Autowired
    private ModelMapper modelMapper;

    private Employee employee;

    private Payroll payroll;

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .name("John")
                .lastname("Doe")
                .email("johndoe@acme.com")
                .password("secretpassword")
                .build();
        payroll = Payroll.builder()
                .salary(123456L)
                .period("01-2024")
                .employee(employee)
                .build();
    }

    @Test
    void canConvertPayrollToPayrollBodyRequest() {
        PayrollDTO body = modelMapper.map(payroll, PayrollDTO.class);

        assertThat(body.getPeriod()).isEqualTo("January-2024");
        assertThat(body.getSalary()).isEqualTo("1234 dollar(s) 56 cent(s)");
        assertThat(body.getName()).isEqualTo(employee.getName());
        assertThat(body.getLastname()).isEqualTo(employee.getLastname());
    }

    @Test
    void canConvertPayrollToPayrollRequestBody (){
        PayrollRequest body = modelMapper.map(payroll, PayrollRequest.class);

        assertThat(body.getPeriod()).isEqualTo(payroll.getPeriod());
        assertThat(body.getSalary()).isEqualTo(payroll.getSalary());
        assertThat(body.getEmployeeEmail()).isEqualTo(employee.getEmail());
    }

    @Test
    void canMapEmployeeToSignupResponse (){
        Employee employee = Employee.builder()
                .id(1L)
                .name("John")
                .lastname("Doe")
                .email("johndoe@acme.com")
                .password("secretpassword")
                .roles(List.of("USER"))
                .build();
        SignupResponse response = modelMapper.map(employee, SignupResponse.class);

        assertThat(response.getName()).isEqualTo(employee.getName());
        assertThat(response.getLastname()).isEqualTo(employee.getLastname());
        assertThat(response.getEmail()).isEqualTo(employee.getEmail());
        assertThat(response.getId()).isEqualTo(employee.getId());
        assertThat(response.getRoles()).isEqualTo(employee.getRoles());
    }

}
