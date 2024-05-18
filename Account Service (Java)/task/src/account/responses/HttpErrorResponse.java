package account.responses;

import java.time.LocalTime;

import static org.springframework.http.HttpStatus.*;


public record HttpErrorResponse(int status, String error, String message, String path, LocalTime timestamp) {
    public  static HttpErrorResponse badRequest (String message, String path){
        return new HttpErrorResponse(BAD_REQUEST.value(), BAD_REQUEST.getReasonPhrase(), message, path, LocalTime.now());
    }
    public  static HttpErrorResponse unauthorized(String message, String path){
        return new HttpErrorResponse(UNAUTHORIZED.value(), UNAUTHORIZED.getReasonPhrase(), message, path, LocalTime.now());
    }

    public static HttpErrorResponse forbidden(String message, String path) {
        return new HttpErrorResponse(FORBIDDEN.value(), FORBIDDEN.getReasonPhrase(), message, path, LocalTime.now());
    }

    public static HttpErrorResponse notFound(String message, String path) {
        return new HttpErrorResponse(NOT_FOUND.value(), NOT_FOUND.getReasonPhrase(), message, path, LocalTime.now());
    }
}