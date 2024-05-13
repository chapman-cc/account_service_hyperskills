package account.dtos;

import java.time.LocalTime;

import static org.springframework.http.HttpStatus.BAD_REQUEST;



public record BadRequestResponse(int status, String error, String message, String path, LocalTime timestamp) {
    public BadRequestResponse(String path) {
        this(null, path);
    }

    public BadRequestResponse(String message, String path) {
        this(BAD_REQUEST.value(), BAD_REQUEST.getReasonPhrase(), message, path, LocalTime.now());
    }
}