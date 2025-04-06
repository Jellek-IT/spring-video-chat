package pl.bronikowski.springchat.backendnotifications.notification.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bronikowski.springchat.backendnotifications.notification.api.dto.NotificationDto;
import pl.bronikowski.springchat.backendnotifications.notification.internal.sender.NotificationSender;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final List<NotificationSender> notificationSenders;

    public void createNotifications(NotificationDto notificationDto) {
        if (notificationRepository.existsByRemoteNotificationId(notificationDto.remoteNotificationId())) {
            return;
        }
        notificationDto.content().getType().getChannels()
                .forEach(channel -> createNotification(notificationDto, channel));
    }

    private void createNotification(NotificationDto notificationDto, NotificationChannel channel) {
        var notification = new Notification(notificationDto.user(), notificationDto.remoteNotificationId(), channel,
                notificationDto.content());
        notificationRepository.save(notification);
        getNotificationSender(channel).send(notification);
    }

    private NotificationSender getNotificationSender(NotificationChannel channel) {
        return notificationSenders.stream()
                .filter(notificationSender -> notificationSender.isApplicable(channel))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Could not find sender for given channel"));
    }
}
