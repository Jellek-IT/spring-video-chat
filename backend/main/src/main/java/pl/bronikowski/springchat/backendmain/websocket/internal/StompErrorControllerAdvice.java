package pl.bronikowski.springchat.backendmain.websocket.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import pl.bronikowski.springchat.backendmain.exception.ExceptionMapper;
import pl.bronikowski.springchat.backendmain.exception.ExceptionResponse;
import pl.bronikowski.springchat.backendmain.websocket.api.StompDestinations;
import pl.bronikowski.springchat.backendmain.websocket.api.StompResponse;

@ControllerAdvice
@RequiredArgsConstructor
public class StompErrorControllerAdvice {
    private final ExceptionMapper exceptionMapper;

    @MessageExceptionHandler
    @SendToUser(destinations = StompDestinations.ERRORS_QUEUE_DESTINATION, broadcast = false)
    public StompResponse<ExceptionResponse> handleException(Throwable e, StompHeaderAccessor stompHeaderAccessor) {
        return StompResponse.message(exceptionMapper.getExceptionResponse(e), stompHeaderAccessor);
    }
}
