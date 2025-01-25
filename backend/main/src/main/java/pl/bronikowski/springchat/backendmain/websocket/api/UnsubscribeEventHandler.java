package pl.bronikowski.springchat.backendmain.websocket.api;

import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

public interface UnsubscribeEventHandler {
    void handleEvent(SessionUnsubscribeEvent event);
}
