package pl.bronikowski.springchat.backendmain.exception;

import org.springframework.http.HttpStatus;

public class AppBadRequestException extends AppApplicationException {
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public AppBadRequestException(String message, ErrorResponseType type) {
        super(message, HTTP_STATUS, type);
    }
}
