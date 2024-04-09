package account.controllers;

import account.controllers.models.Employee;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> signUp(@Valid @RequestBody Employee employee) {
        return Map.of(
                "name", employee.getName(),
                "lastname", employee.getLastname(),
                "email", employee.getEmail()
        );
    }

    @PostMapping("/changepass")
    public void changePassword() {

    }

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
//        return Map.of("error", ex.getMessage());
//    }
}
