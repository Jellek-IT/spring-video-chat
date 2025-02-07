package pl.bronikowski.springchat.backendmain.websocket.internal;

import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.util.UriTemplate;
import pl.bronikowski.springchat.backendmain.exception.AppBadRequestException;

import java.util.Map;

public class StompDestinationMatcher {
    private final UriTemplate uriTemplate;
    private final StompCommand command;

    public StompDestinationMatcher(@NonNull StompCommand command, @NonNull String template) {
        this.command = command;
        this.uriTemplate = new UriTemplate(template);
    }

    public boolean matches(Message<?> message) {
        var accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if(accessor == null) {
            throw new AppBadRequestException("message headers was null");
        }
        return command.equals(accessor.getCommand()) && uriTemplate.matches(accessor.getDestination());
    }

    public Map<String, String> getParameters(Message<?> message) {
        var accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if(accessor == null) {
            throw new AppBadRequestException("message headers was null");
        }
        return uriTemplate.match(accessor.getDestination());
    }
}
