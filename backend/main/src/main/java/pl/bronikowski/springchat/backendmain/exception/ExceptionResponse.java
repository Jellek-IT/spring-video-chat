package pl.bronikowski.springchat.backendmain.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class ExceptionResponse {
    private final String message;
    private final Instant date;
    private final List<ExceptionTypeDto> types;

    @JsonCreator
    public ExceptionResponse(String message, Instant date, List<ExceptionTypeDto> types) {
        this.message = message == null || message.length() <= 10000 ? message : message.substring(0, 1000);
        this.date = date;
        this.types = types;
    }

    public ExceptionResponse(String message, Instant date) {
        this(message, date, null);
    }
}
