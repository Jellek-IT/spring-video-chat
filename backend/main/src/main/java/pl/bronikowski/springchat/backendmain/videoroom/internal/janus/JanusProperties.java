package pl.bronikowski.springchat.backendmain.videoroom.internal.janus;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.client.janus")
public record JanusProperties(
        String wsHost,
        String tokenAuthSecret,
        String videoRoomAdminKey,
        Integer publishers,
        Duration appAuthTokenLifetime,
        Duration clientAuthTokenLifetime,
        Duration videoRoomLifeTime
) {
}
