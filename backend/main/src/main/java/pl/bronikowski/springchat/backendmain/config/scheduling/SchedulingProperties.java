package pl.bronikowski.springchat.backendmain.config.scheduling;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.scheduling")
public record SchedulingProperties(
        Integer threadPoolSize
) {
}
