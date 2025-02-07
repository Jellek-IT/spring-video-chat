package pl.bronikowski.springchat.backendmain.websocket.api;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import pl.bronikowski.springchat.backendmain.exception.AppBadRequestException;

public record UserConnectionDetails(
        String authResourceId,
        String stompSessionId
) {
    public static UserConnectionDetails fromMessage(Message<?> message, String authResourceId) {
        var accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            throw new AppBadRequestException("message header was null");
        }
        return new UserConnectionDetails(authResourceId, accessor.getSessionId());
    }

    public static UserConnectionDetails fromSessionDisconnectEvent(SessionDisconnectEvent event) {
        return new UserConnectionDetails(event.getUser().getName(), event.getSessionId());
    }
}
