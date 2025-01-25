package pl.bronikowski.springchat.backendmain.websocket.api;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;

public record UserConnectionDetails(
        String authResourceId,
        String stompSessionId
) {
    public static UserConnectionDetails fromMessage(Message<?> message, String authResourceId) {
        var accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        return new UserConnectionDetails(authResourceId, accessor.getSessionId());
    }

    public static UserConnectionDetails fromSubProtocolEvent(AbstractSubProtocolEvent event) {
        var message = event.getMessage();
        return fromMessage(message, event.getUser().getName());
    }
}
