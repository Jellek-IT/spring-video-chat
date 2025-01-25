package pl.bronikowski.springchat.backendmain.websocket.api;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import pl.bronikowski.springchat.backendmain.exception.AppInternalServerErrorException;
import pl.bronikowski.springchat.backendmain.exception.ExceptionMapper;

@Component
@RequiredArgsConstructor
public class StompMessagingTemplate {
    private final SimpMessagingTemplate template;
    private final ExceptionMapper exceptionMapper;

    public void convertAndSendErrorToCurrentUser(@Nullable String authResourceId, Throwable t,
                                                 Message<?> incommingMessage) {
        var body = exceptionMapper.getExceptionResponse(t);
        var payload = StompResponse.error(body, incommingMessage);
        this.convertAndSendToCurrentUser(authResourceId, StompDestinations.ERRORS_QUEUE_DESTINATION, payload,
                incommingMessage);
    }

    public void convertAndSendToCurrentUser(@Nullable String authResourceId, String destination, Object payload,
                                            Message<?> incommingMessage)
            throws MessagingException {
        var messageHeaders = MessageHeaderAccessor.getAccessor(incommingMessage, StompHeaderAccessor.class);
        var sessionId = messageHeaders.getSessionId();
        var receiver = authResourceId != null ? authResourceId : sessionId;
        if (receiver == null) {
            throw new AppInternalServerErrorException("Could not guess user destination");
        }
        var headers = createHeaders(messageHeaders.getSessionId());
        template.convertAndSendToUser(receiver, destination, payload, headers);
    }

    private MessageHeaders createHeaders(@Nullable String sessionId) {
        var headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        if (sessionId != null) {
            headerAccessor.setSessionId(sessionId);
        }
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }

}
