package account.responses;

import java.time.LocalTime;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public record SignupBodyNotValidResponse(int status, String error, String path, LocalTime timestamp) {
    public SignupBodyNotValidResponse(String path) {
        this(BAD_REQUEST.value(), BAD_REQUEST.getReasonPhrase(), path, LocalTime.now());
    }
}
