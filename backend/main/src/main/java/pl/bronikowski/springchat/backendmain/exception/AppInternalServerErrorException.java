package pl.bronikowski.springchat.backendmain.exception;

import org.springframework.http.HttpStatus;

public class AppInternalServerErrorException extends AppApplicationException {
    private static final HttpStatus HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

    public AppInternalServerErrorException(String message) {
        super(message, HTTP_STATUS);
    }

    public AppInternalServerErrorException(String message, Throwable cause) {
        super(message, cause, HTTP_STATUS);
    }

}
