package account.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class BusinessController {
    @GetMapping("/empl/payment")
    public void getEmployeePayroll() {

    }

    @PostMapping("/acct/payments")
    public void uploadEmployeePayroll() {

    }

    @PutMapping("/acct/payments")
    public void updateEmployeePayroll() {

    }
}
