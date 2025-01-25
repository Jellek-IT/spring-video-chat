package pl.bronikowski.springchat.backendmain.websocket.api;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import pl.bronikowski.springchat.backendmain.websocket.internal.StompDestinationMatcher;

import java.util.Map;

public abstract class DestinationMatchedSubscribeEventHandler {
    private final StompDestinationMatcher destinationMatcher;

    public DestinationMatchedSubscribeEventHandler() {
        var template = getTemplate();
        assert template != null;
        destinationMatcher = new StompDestinationMatcher(StompCommand.SUBSCRIBE, template);
    }

    protected abstract String getTemplate();

    public boolean matches(Message<?> message) {
        return destinationMatcher.matches(message);
    }

    public void handleEvent(SessionSubscribeEvent event) {
        handleEvent(destinationMatcher.getParameters(event.getMessage()), event);
    }

    protected abstract void handleEvent(Map<String, String> parameters, SessionSubscribeEvent event);
}
