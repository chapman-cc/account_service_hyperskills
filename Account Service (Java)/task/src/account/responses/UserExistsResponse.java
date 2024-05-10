package account.responses;

import java.time.LocalTime;

import static org.springframework.http.HttpStatus.BAD_REQUEST;


public record UserExistsResponse(int status, String error, String message, String path, LocalTime timestamp) {
    public UserExistsResponse(String path) {
        this(BAD_REQUEST.value(), BAD_REQUEST.getReasonPhrase(), "User exist!", path, LocalTime.now());
    }
}