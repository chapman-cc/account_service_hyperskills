package account.controllers;

import account.dtos.NewPasswordDTO;
import account.dtos.PasswordChangedResponse;
import account.dtos.SignupResponse;
import account.models.Employee;
import account.services.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final EmployeeService service;

    public AuthenticationController(EmployeeService employeeService) {
        this.service = employeeService;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.OK)
    public SignupResponse signUp(@Valid @RequestBody Employee employee) {
        return service.register(employee);
    }

    @PostMapping("/changepass")
    @ResponseStatus(HttpStatus.OK)
    public PasswordChangedResponse changePassword(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody NewPasswordDTO body) {
        return service.updatePassword(userDetails.getUsername(), body.getPassword());
    }
}
