package pl.bronikowski.springchat.backendmain.notification.internal.kafka;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

public enum NotificationType {
    USER_REGISTERED;

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Constants {
        public static final String USER_REGISTERED = "USER_REGISTERED";
    }
}
