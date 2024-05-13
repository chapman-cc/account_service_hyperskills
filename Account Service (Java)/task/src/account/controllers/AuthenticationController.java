package account.controllers;


import account.dtos.BadRequestResponse;
import account.dtos.NewPasswordDTO;
import account.dtos.PasswordChangedResponse;
import account.dtos.SignupResponse;
import account.exceptions.BreachedPasswordDetectedException;
import account.exceptions.PasswordNotChangedException;
import account.exceptions.UserAlreadyExistsException;
import account.models.Employee;
import account.services.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
        Optional<Employee> found = service.findByEmail(employee.getEmail());
        if (found.isPresent()) {
            throw new UserAlreadyExistsException();
        }

        Employee registered = service.register(employee);

        return new SignupResponse(
                registered.getId(),
                registered.getName(),
                registered.getLastname(),
                registered.getEmail()
        );
    }

    @PostMapping("/changepass")
    @ResponseStatus(HttpStatus.OK)
    public PasswordChangedResponse changePassword(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody NewPasswordDTO body) {
        Employee updated = service.updatePassword(userDetails.getUsername(), body.getPassword());
        return new PasswordChangedResponse(updated.getEmail());
    }

    @ExceptionHandler({
            UserAlreadyExistsException.class,
            BreachedPasswordDetectedException.class,
            PasswordNotChangedException.class,
            RuntimeException.class
    })
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public BadRequestResponse handleRuntimeException(HttpServletRequest req, RuntimeException e) {
        return new BadRequestResponse(e.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public BadRequestResponse handleMethodArgumentNotValidException(HttpServletRequest req, MethodArgumentNotValidException e) {
        String requestURI = req.getRequestURI();
        String message = e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList().get(0);
        return new BadRequestResponse(message, requestURI);
    }
}
