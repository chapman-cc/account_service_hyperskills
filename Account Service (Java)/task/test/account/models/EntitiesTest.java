package account.models;

import account.utils.EmployeeFaker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class EntitiesTest {

    @Autowired
    private EmployeeFaker faker;

    @Test
    void EmployeeCanGetPayrollThroughEmployee() {
        Employee employee = faker.generateEmployee();

        assertThat(employee.getPayrolls()).isNotNull();
        assertThat(employee.getPayrolls()).size().isEqualTo(0);

        Payroll payroll = faker.generaPayroll();

        employee.addPayroll(payroll);

        assertThat(employee.getPayrolls()).size().isEqualTo(1);
        assertThat(employee.getPayrolls().get(0)).isEqualTo(payroll);
    }

    @Test
    void PayrollCanGetEmployeeThroughPayroll() {
        Employee employee = faker.generateEmployee();
        Payroll payroll = faker.generaPayroll();

        payroll.setEmployee(employee);

        assertThat(payroll.getEmployee()).isNotNull();
        assertThat(employee.getPayrolls()).size().isEqualTo(1);
        assertThat(employee.getPayrolls().get(0)).isEqualTo(payroll);
    }
}
