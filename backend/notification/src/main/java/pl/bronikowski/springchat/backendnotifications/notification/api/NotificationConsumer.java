package pl.bronikowski.springchat.backendnotifications.notification.api;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.bronikowski.springchat.backendnotifications.notification.api.dto.NotificationDto;
import pl.bronikowski.springchat.backendnotifications.notification.internal.NotificationService;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {
    private final NotificationService notificationService;

    @KafkaListener(topics = "${app.kafka.topic.notification.name}",
            containerFactory = "notificationKafkaListenerContainerFactory",
            batch = "true")
    public void consume(ConsumerRecords<String, NotificationDto> records) {
        records.forEach(record ->
                this.notificationService.createNotifications(record.value()));
    }
}
