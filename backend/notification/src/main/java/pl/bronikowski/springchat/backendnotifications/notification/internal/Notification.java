package pl.bronikowski.springchat.backendnotifications.notification.internal;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.bronikowski.springchat.backendnotifications.notification.api.NotificationStatus;
import pl.bronikowski.springchat.backendnotifications.notification.api.dto.NotificationUserDto;
import pl.bronikowski.springchat.backendnotifications.notification.api.dto.content.NotificationContent;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Document
@Getter
@Setter
public class Notification {
    @Id
    private String id;
    @Indexed
    private UUID remoteNotificationId;
    private NotificationUser user;
    private NotificationStatus status;
    private NotificationChannel channel;
    private NotificationContent content;
    private String error;
    private Instant processedAt;

    public Notification(NotificationUserDto userDto, UUID remoteNotificationId, NotificationChannel channel,
                        NotificationContent content) {
        this.user = new NotificationUser(userDto.id(), userDto.email());
        this.remoteNotificationId = remoteNotificationId;
        this.status = NotificationStatus.PENDING;
        this.channel = channel;
        this.content = content;
    }

    public void setStatusSent(Clock clock) {
        this.processedAt = clock.instant();
        this.status = NotificationStatus.SENT;
    }

    public void setStatusError(Clock clock, Throwable t) {
        this.processedAt = clock.instant();
        this.status = NotificationStatus.ERROR;
        this.error = t.getMessage();
    }
}
