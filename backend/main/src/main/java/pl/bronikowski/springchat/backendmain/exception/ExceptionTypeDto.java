package pl.bronikowski.springchat.backendmain.exception;

import java.util.ArrayList;
import java.util.List;

public record ExceptionTypeDto(
        String type,
        List<String> values
) {
    public ExceptionTypeDto(String type) {
        this(type, new ArrayList<>());
    }
}
