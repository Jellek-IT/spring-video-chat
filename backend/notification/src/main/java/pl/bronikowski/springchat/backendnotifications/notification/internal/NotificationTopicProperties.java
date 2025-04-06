package pl.bronikowski.springchat.backendnotifications.notification.internal;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka.topic.notification")
public record NotificationTopicProperties(
        String name,
        int partitions,
        int replicas
) {
}
