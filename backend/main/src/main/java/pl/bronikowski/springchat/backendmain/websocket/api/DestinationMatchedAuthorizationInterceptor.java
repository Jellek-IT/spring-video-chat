package pl.bronikowski.springchat.backendmain.websocket.api;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import pl.bronikowski.springchat.backendmain.websocket.internal.StompDestinationMatcher;

import java.util.Map;

public abstract class DestinationMatchedAuthorizationInterceptor {
    private final StompDestinationMatcher destinationMatcher;

    public DestinationMatchedAuthorizationInterceptor() {
        var template = getTemplate();
        assert template != null;
        var stompCommand = getCommand();
        assert stompCommand != null;
        destinationMatcher = new StompDestinationMatcher(stompCommand, template);
    }

    protected abstract String getTemplate();

    protected abstract StompCommand getCommand();

    public boolean matches(Message<?> message) {
        return destinationMatcher.matches(message);
    }

    public boolean hasAccess(Message<?> message) {
        return hasAccess(destinationMatcher.getParameters(message));
    }

    protected abstract boolean hasAccess(Map<String, String> parameters);
}
