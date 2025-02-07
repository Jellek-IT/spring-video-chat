package pl.bronikowski.springchat.backendmain.exception;

import jakarta.annotation.Nullable;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import pl.bronikowski.springchat.backendmain.shared.utils.StringUtils;

import java.time.Clock;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ExceptionMapper {
    private final Clock clock;

    public ExceptionResponse mapToExceptionResponse(AppApplicationException e) {
        var types = e.getType() != null
                ? List.of(new ExceptionTypeDto(e.getType().name(), e.getValues()))
                : Collections.<ExceptionTypeDto>emptyList();

        return new ExceptionResponse(e.getMessage(), clock.instant(), types);
    }

    public ExceptionResponse mapToExceptionResponse(MethodArgumentNotValidException e) {
        var types = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getCode)
                .filter(Objects::nonNull)
                .map(StringUtils::camelCaseToEnum)
                .map(ExceptionTypeDto::new)
                .toList();
        return new ExceptionResponse(e.getMessage(), clock.instant(), types);
    }

    public ExceptionResponse mapToExceptionResponse(org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException e) {
        var types = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getCode)
                .filter(Objects::nonNull)
                .map(StringUtils::camelCaseToEnum)
                .map(ExceptionTypeDto::new)
                .toList();
        return new ExceptionResponse(e.getMessage(), clock.instant(), types);
    }

    public ExceptionResponse mapToExceptionResponse(ConstraintViolationException e) {
        var types = e.getConstraintViolations().stream()
                .map(StringUtils::toString)
                .map(ExceptionTypeDto::new)
                .toList();
        return new ExceptionResponse(e.getMessage(), clock.instant(), types);
    }

    public ExceptionResponse mapToGenericExceptionResponse(@Nullable Throwable e) {
        var message = e != null ? e.getMessage() : null;
        return new ExceptionResponse(message, clock.instant());
    }

    public ExceptionResponse getExceptionResponse(Throwable t) {
        return switch (t) {
            case AppApplicationException appApplicationException -> mapToExceptionResponse(appApplicationException);

            case MethodArgumentNotValidException methodArgumentNotValidException ->
                    mapToExceptionResponse(methodArgumentNotValidException);

            case org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException methodArgumentNotValidException ->
                    mapToExceptionResponse(methodArgumentNotValidException);

            case ConstraintViolationException constraintViolationException ->
                    mapToExceptionResponse(constraintViolationException);

            case null, default -> mapToGenericExceptionResponse(t);
        };
    }
}
