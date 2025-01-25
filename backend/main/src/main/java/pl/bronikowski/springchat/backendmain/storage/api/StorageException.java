package pl.bronikowski.springchat.backendmain.storage.api;

import org.springframework.http.HttpStatus;
import pl.bronikowski.springchat.backendmain.exception.AppApplicationException;

public class StorageException extends AppApplicationException {
    private static final HttpStatus HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

    public StorageException(String message, Throwable cause) {
        super(message, cause, HTTP_STATUS);
    }
}
