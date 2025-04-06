package pl.bronikowski.springchat.backendmain.notification.internal.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.bronikowski.springchat.backendmain.notification.api.NotificationClient;
import pl.bronikowski.springchat.backendmain.notification.internal.kafka.dto.NotificationDto;
import pl.bronikowski.springchat.backendmain.notification.internal.kafka.dto.NotificationUserDto;
import pl.bronikowski.springchat.backendmain.notification.internal.kafka.dto.content.NotificationContent;
import pl.bronikowski.springchat.backendmain.notification.internal.kafka.dto.content.UserRegisteredNotificationContent;
import pl.bronikowski.springchat.backendmain.user.internal.User;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KafkaNotificationClient implements NotificationClient {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaNotificationTopicProperties kafkaNotificationTopicProperties;

    @Override
    public void sendUserRegisteredNotification(User user, String token) {
        var notificationContent = new UserRegisteredNotificationContent(token);
        sendNotification(user, notificationContent);
    }

    private void sendNotification(User user, NotificationContent content) {
        var userDto = new NotificationUserDto(user.getId(), user.getEmail());
        var notificationDto = new NotificationDto(userDto, UUID.randomUUID(), content);
        kafkaTemplate.send(kafkaNotificationTopicProperties.name(),
                user.getId().toString(),
                notificationDto);
    }
}
