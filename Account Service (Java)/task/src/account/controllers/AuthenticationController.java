package account.controllers;

import account.exceptions.UserAlreadyExistsException;
import account.models.Employee;
import account.responses.SignupBodyNotValidResponse;
import account.responses.SignupResponse;
import account.responses.UserExistsResponse;
import account.services.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    public void changePassword() {

    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public UserExistsResponse handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        return new UserExistsResponse("/api/auth/signup");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public SignupBodyNotValidResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return new SignupBodyNotValidResponse("/api/auth/signup");
    }
}
