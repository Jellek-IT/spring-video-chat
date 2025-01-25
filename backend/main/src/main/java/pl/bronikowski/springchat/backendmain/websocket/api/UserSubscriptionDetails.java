package pl.bronikowski.springchat.backendmain.websocket.api;

import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;

public record UserSubscriptionDetails(
        String authResourceId,
        String stompSessionId,
        String stompSubscriptionId
) {
    public static UserSubscriptionDetails fromSubProtocolEvent(AbstractSubProtocolEvent event) {
        var message = event.getMessage();
        var accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        return new UserSubscriptionDetails(event.getUser().getName(), accessor.getSessionId(),
                accessor.getSubscriptionId());
    }
}
