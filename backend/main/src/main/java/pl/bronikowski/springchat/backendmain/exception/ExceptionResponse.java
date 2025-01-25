package pl.bronikowski.springchat.backendmain.exception;

import java.time.Instant;
import java.util.List;


public record ExceptionResponse(
        String message,
        Instant date,
        List<ExceptionTypeDto> types
) {
    public ExceptionResponse(String message, Instant date) {
        this(message, date, null);
    }
}
