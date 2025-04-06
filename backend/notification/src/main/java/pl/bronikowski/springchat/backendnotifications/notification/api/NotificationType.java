package pl.bronikowski.springchat.backendnotifications.notification.api;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import pl.bronikowski.springchat.backendnotifications.notification.internal.NotificationChannel;

import java.util.Set;

@RequiredArgsConstructor
@Getter
public enum NotificationType {
    USER_REGISTERED(Set.of(NotificationChannel.EMAIL));

    private final Set<NotificationChannel> channels;

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Constants {
        public static final String USER_REGISTERED = "USER_REGISTERED";
    }
}
