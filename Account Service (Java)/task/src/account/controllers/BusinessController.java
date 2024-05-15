package account.controllers;

import account.dtos.PayrollDTO;
import account.dtos.PayrollResponse;
import account.models.Payroll;
import account.services.PayrollService;
import account.utils.Regex;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
public class BusinessController {

    private final PayrollService payrollService;

    @Autowired
    public BusinessController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @GetMapping("/hello-world")
    public String helloWorld() {
        return "Hello World!";
    }

    @GetMapping("/empl/payment")
    @ResponseStatus(HttpStatus.OK)
    public List<PayrollDTO> getEmployeePayrolls(@AuthenticationPrincipal UserDetails user) {
        if (user == null) {
            throw new UsernameNotFoundException("You are not authenticated");
        }

        return payrollService.getPayroll(user.getUsername());
    }

    @GetMapping(value = "/empl/payment", params = "period")
    @ResponseStatus(HttpStatus.OK)
    public PayrollDTO getEmployeePayrollOfPeriod(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam @Pattern(regexp = Regex.PAYROLL_PERIOD) String period
    ) {
        if (user == null) {
            throw new UsernameNotFoundException("You are not authenticated");
        }
        return payrollService.getPayroll(user.getUsername(), period);
    }

    @PostMapping("/acct/payments")
    @ResponseStatus(HttpStatus.OK)
    public PayrollResponse uploadEmployeePayroll(@RequestBody @Valid List<Payroll> payrolls) {
        payrollService.savePayrolls(payrolls);
        return new PayrollResponse("Added successfully!");
    }

    @PutMapping("/acct/payments")
    public PayrollResponse updateEmployeePayroll(@Valid @RequestBody Payroll payroll) {
        payrollService.updatePayroll(payroll);
        return new PayrollResponse("Updated successfully!");
    }
}
