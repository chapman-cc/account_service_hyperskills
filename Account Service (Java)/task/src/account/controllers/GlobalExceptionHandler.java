package account.controllers;

import account.exceptions.EmployeeNotFoundException;
import account.exceptions.RoleNotFoundException;
import account.responses.HttpErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<HttpErrorResponse> handleConstraintViolationException(HttpServletRequest req, ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).toList().get(0);
        String requestURI = req.getRequestURI();
        HttpErrorResponse body = HttpErrorResponse.badRequest(message, requestURI);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<HttpErrorResponse> handleMethodArgumentNotValidException(HttpServletRequest req, MethodArgumentNotValidException e) {
        String requestURI = req.getRequestURI();
        String message = e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(", "));
        HttpErrorResponse body = HttpErrorResponse.badRequest(message, requestURI);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler({
            EmployeeNotFoundException.class,
            RoleNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<HttpErrorResponse> handleNotFoundException(HttpServletRequest req, RuntimeException e) {
        String requestURI = req.getRequestURI();
        HttpErrorResponse body = HttpErrorResponse.notFound(e.getMessage(), requestURI);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }



    //<YOUR_GithubPersonalAccessToken_HERE
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<HttpErrorResponse> handleRuntimeException(HttpServletRequest req, RuntimeException e) {

        String message = e.getMessage();
        String requestURI = req.getRequestURI();
        HttpErrorResponse body = HttpErrorResponse.badRequest(message, requestURI);
        return ResponseEntity.badRequest().body(body);
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<HttpErrorResponse> handleDataIntegrityViolationException(HttpServletRequest req, DataIntegrityViolationException e) {
        String message = e.getMessage();
        String requestURI = req.getRequestURI();
        HttpErrorResponse body = HttpErrorResponse.badRequest(message, requestURI);
        return ResponseEntity.badRequest().body(body);
    }
}
