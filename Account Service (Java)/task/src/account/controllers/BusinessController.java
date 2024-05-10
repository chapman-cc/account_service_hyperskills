package account.controllers;

import account.models.Employee;
import account.responses.SignupResponse;
import account.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class BusinessController {
    private EmployeeService employeeService;

    @Autowired
    public BusinessController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }


    @GetMapping("/empl/payment")
    @ResponseStatus(HttpStatus.OK)
    public SignupResponse getEmployeePayroll(@AuthenticationPrincipal UserDetails user) {
        Optional<Employee> optional = employeeService.findByEmail(user.getUsername());

        if (optional.isEmpty()) {
            throw new RuntimeException("Employee not found");
        }

        Employee employee = optional.get();

        return new SignupResponse(employee.getId(), employee.getName(), employee.getLastname(), employee.getEmail());
    }

    @PostMapping("/acct/payments")
    public void uploadEmployeePayroll() {

    }

    @PutMapping("/acct/payments")
    public void updateEmployeePayroll() {

    }
}
