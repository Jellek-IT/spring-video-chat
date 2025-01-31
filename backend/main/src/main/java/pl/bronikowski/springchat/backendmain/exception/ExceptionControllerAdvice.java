package pl.bronikowski.springchat.backendmain.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.ForbiddenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionControllerAdvice {
    private final ExceptionMapper exceptionMapper;

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ExceptionResponse handleJpaObjectRetrievalFailureException(JpaObjectRetrievalFailureException e) throws Exception {
        log.error("Handle exception", e);
        if (e.getCause() instanceof EntityNotFoundException) {
            return exceptionMapper.mapToGenericExceptionResponse(e);
        } else {
            throw e;
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ExceptionResponse on(EntityNotFoundException e) {
        log.error("Handle exception", e);
        return exceptionMapper.mapToGenericExceptionResponse(e);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler
    public ExceptionResponse on(ForbiddenException e) {
        return exceptionMapper.mapToGenericExceptionResponse(e);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ExceptionResponse on(ConstraintViolationException e) {
        return exceptionMapper.mapToExceptionResponse(e);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ExceptionResponse on(MethodArgumentNotValidException e) {
        return exceptionMapper.mapToExceptionResponse(e);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> on(AppApplicationException e) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(e.getStatusCode())) {
            log.error("ApplicationException occurs", e);
        }

        var response = exceptionMapper.mapToExceptionResponse(e);
        return ResponseEntity.status(e.getStatusCode()).body(response);
    }
}
