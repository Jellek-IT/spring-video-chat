package pl.bronikowski.springchat.backendmain.config.async;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.async")
public record AsyncProperties(
        Integer corePoolSize
) {
}
