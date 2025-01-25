package pl.bronikowski.springchat.backendmain.websocket.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;
import pl.bronikowski.springchat.backendmain.websocket.api.DestinationMatchedSubscribeEventHandler;
import pl.bronikowski.springchat.backendmain.websocket.api.SessionDisconnectEventHandler;
import pl.bronikowski.springchat.backendmain.websocket.api.UnsubscribeEventHandler;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StompEventListener {
    private final List<DestinationMatchedSubscribeEventHandler> destinationMatchedSubscribeEventHandlers;
    private final List<UnsubscribeEventHandler> unsubscribeEventHandlers;
    private final List<SessionDisconnectEventHandler> sessionDisconnectEventHandlers;

    @EventListener
    public void onSessionSubscribeEvent(SessionSubscribeEvent event) {
        this.destinationMatchedSubscribeEventHandlers.stream()
                .filter(messageHandler -> messageHandler.matches(event.getMessage()))
                .forEach(messageHandler -> messageHandler.handleEvent(event));
    }

    @EventListener
    public void onSessionUnsubscribeEvent(SessionUnsubscribeEvent event) {
        this.unsubscribeEventHandlers.forEach(handler -> handler.handleEvent(event));
    }

    @EventListener
    public void onSessionDisconnected(SessionDisconnectEvent event) {
        this.sessionDisconnectEventHandlers.forEach(handler -> handler.handleEvent(event));
    }
}
