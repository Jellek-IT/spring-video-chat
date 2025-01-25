package pl.bronikowski.springchat.backendmain.authserver.api;

import org.springframework.http.HttpStatus;
import pl.bronikowski.springchat.backendmain.exception.AppApplicationException;

public class AuthClientException extends AppApplicationException {
    private static final HttpStatus HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

    public AuthClientException(String message) {
        super(message, HTTP_STATUS);
    }

    public AuthClientException(String message, Throwable t) {
        super(message, t, HTTP_STATUS);
    }
}
