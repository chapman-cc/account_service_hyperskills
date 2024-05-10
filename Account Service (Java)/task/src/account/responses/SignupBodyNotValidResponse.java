package account.responses;

import org.springframework.http.HttpStatus;

import java.time.LocalTime;

public record SignupBodyNotValidResponse(int status, String error, String path, LocalTime timestamp) {
    public SignupBodyNotValidResponse(String path) {
        this(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), path, LocalTime.now());
    }
}
