package pl.bronikowski.springchat.backendmain.websocket.internal;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.client.stomp-broker")
public record StompBrokerProperties(
        Boolean enabled,
        String relayHost,
        Integer relayPort,
        String clientLogin,
        String clientPasscode
) {
}
