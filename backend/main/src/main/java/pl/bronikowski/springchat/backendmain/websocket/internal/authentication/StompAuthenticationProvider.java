package pl.bronikowski.springchat.backendmain.websocket.internal.authentication;

import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

public interface StompAuthenticationProvider {
    void authenticate(StompHeaderAccessor accessor);
}
