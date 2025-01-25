package pl.bronikowski.springchat.backendmain.websocket.api;

import org.springframework.web.socket.messaging.SessionDisconnectEvent;

public interface SessionDisconnectEventHandler {
    void handleEvent(SessionDisconnectEvent event);
}
