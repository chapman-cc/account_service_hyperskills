package account.controllers;

import account.dtos.BadRequestResponse;
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

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<BadRequestResponse> handleConstraintViolationException(HttpServletRequest req, ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).toList().get(0);
        String requestURI = req.getRequestURI();
        return ResponseEntity.badRequest().body(new BadRequestResponse(message, requestURI));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<BadRequestResponse> handleMethodArgumentNotValidException(HttpServletRequest req, MethodArgumentNotValidException e) {
        String requestURI = req.getRequestURI();
        String message = e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList().get(0);
        return ResponseEntity.badRequest().body(new BadRequestResponse(message, requestURI));
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<BadRequestResponse> handleRuntimeException(HttpServletRequest req, RuntimeException e) {
        String message = e.getMessage();
        String requestURI = req.getRequestURI();
        return ResponseEntity.badRequest().body(new BadRequestResponse(message, requestURI));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<BadRequestResponse> handleDataIntegrityViolationException(HttpServletRequest req, DataIntegrityViolationException e) {
        String message = e.getMessage();
        String requestURI = req.getRequestURI();
        return ResponseEntity.badRequest().body(new BadRequestResponse(message, requestURI));
    }
}
