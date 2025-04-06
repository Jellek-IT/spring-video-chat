package pl.bronikowski.springchat.backendmain.notification.internal.kafka.dto.content;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.bronikowski.springchat.backendmain.notification.internal.kafka.NotificationType;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Getter
public class NotificationContent {
    private final NotificationType type;
}
