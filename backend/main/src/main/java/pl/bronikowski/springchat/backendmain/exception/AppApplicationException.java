package pl.bronikowski.springchat.backendmain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public abstract class AppApplicationException extends RuntimeException {
    private final HttpStatus statusCode;
    private final ExceptionResponseType type;
    private final List<String> values;

    protected AppApplicationException(HttpStatus statusCode) {
        super();
        this.statusCode = statusCode;
        this.type = null;
        this.values = null;
    }

    protected AppApplicationException(String message, HttpStatus statusCode) {
        super(message);
        this.statusCode = statusCode;
        this.type = null;
        this.values = null;
    }

    protected AppApplicationException(String message, HttpStatus statusCode, ExceptionResponseType type) {
        super(message);
        this.statusCode = statusCode;
        this.type = type;
        this.values = null;
    }

    protected AppApplicationException(String message, Throwable cause, HttpStatus statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
        this.type = null;
        this.values = null;
    }

    protected AppApplicationException(String message, Throwable cause, HttpStatus statusCode,
                                      ExceptionResponseType type) {
        super(message, cause);
        this.statusCode = statusCode;
        this.type = type;
        this.values = null;
    }

    protected AppApplicationException(String message, HttpStatus statusCode, ExceptionResponseType type,
                                      List<String> values) {
        super(message);
        this.statusCode = statusCode;
        this.type = type;
        this.values = values;
    }
}
