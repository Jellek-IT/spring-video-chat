package pl.bronikowski.springchat.backendmain.exception;

import org.springframework.http.HttpStatus;

public class AppNotFoundException extends AppApplicationException {
    private static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;

    public AppNotFoundException() {
        super(HTTP_STATUS);
    }
}
