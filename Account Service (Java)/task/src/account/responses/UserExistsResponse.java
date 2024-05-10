package account.responses;

import org.springframework.http.HttpStatus;

import java.time.LocalTime;

public record UserExistsResponse(int status, String error, String message, String path, LocalTime timestamp) {
    public UserExistsResponse(String path) {
        this(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "User exist!",
                path,
                LocalTime.now()
        );
    }
}