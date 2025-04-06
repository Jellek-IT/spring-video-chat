package pl.bronikowski.springchat.backendnotifications.shared.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.platform")
public record PlatformProperties(
        String url,
        String email,
        String prefix
) {
}
