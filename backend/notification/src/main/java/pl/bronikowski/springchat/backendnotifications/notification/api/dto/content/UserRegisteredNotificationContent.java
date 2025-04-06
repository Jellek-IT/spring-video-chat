package pl.bronikowski.springchat.backendnotifications.notification.api.dto.content;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.bronikowski.springchat.backendnotifications.notification.api.NotificationType;

@Getter
@EqualsAndHashCode(callSuper = true)
public class UserRegisteredNotificationContent extends NotificationContent {
    private final String token;

    public UserRegisteredNotificationContent(@JsonProperty("token") String token) {
        super(NotificationType.USER_REGISTERED);
        this.token = token;
    }
}
