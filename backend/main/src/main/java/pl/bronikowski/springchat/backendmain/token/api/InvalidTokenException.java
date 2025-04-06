package pl.bronikowski.springchat.backendmain.token.api;

import org.springframework.http.HttpStatus;
import pl.bronikowski.springchat.backendmain.exception.AppApplicationException;
import pl.bronikowski.springchat.backendmain.exception.ExceptionResponseType;

public class InvalidTokenException extends AppApplicationException {
    private static final HttpStatus HTTP_STATUS = HttpStatus.FORBIDDEN;

    public InvalidTokenException() {
        super("Invalid token", HTTP_STATUS, ExceptionResponseType.INVALID_TOKEN);
    }
}
