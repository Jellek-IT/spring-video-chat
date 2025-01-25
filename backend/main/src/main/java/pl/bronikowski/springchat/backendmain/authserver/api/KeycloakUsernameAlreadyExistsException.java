package pl.bronikowski.springchat.backendmain.authserver.api;

import org.springframework.http.HttpStatus;
import pl.bronikowski.springchat.backendmain.exception.AppApplicationException;

public class KeycloakUsernameAlreadyExistsException extends AppApplicationException {
    private static final HttpStatus HTTP_STATUS = HttpStatus.CONFLICT;

    public KeycloakUsernameAlreadyExistsException(String email) {
        super("User: " + email + " already exists", HTTP_STATUS);
    }
}
