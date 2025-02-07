package pl.bronikowski.springchat.backendmain.config.stomp

import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.security.core.Authentication
import pl.bronikowski.springchat.backendmain.websocket.internal.authentication.StompAuthenticationProvider

import java.util.concurrent.atomic.AtomicReference

class StompTestAuthenticationProvider implements StompAuthenticationProvider {
    private final AtomicReference<Authentication> principal = new AtomicReference<>(null)

    @Override
    void authenticate(StompHeaderAccessor accessor) {
        accessor.setUser(principal.get())
    }

    void setPrincipal(Authentication principal) {
        this.principal.set(principal)
    }
}
