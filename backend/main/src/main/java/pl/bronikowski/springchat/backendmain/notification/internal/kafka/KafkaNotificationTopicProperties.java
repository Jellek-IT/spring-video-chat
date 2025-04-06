package pl.bronikowski.springchat.backendmain.notification.internal.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka.topic.notification")
public record KafkaNotificationTopicProperties(
        String name
) {
}
