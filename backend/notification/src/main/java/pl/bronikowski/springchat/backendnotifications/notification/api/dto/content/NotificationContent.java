package pl.bronikowski.springchat.backendnotifications.notification.api.dto.content;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.bronikowski.springchat.backendnotifications.notification.api.NotificationType;

@JsonIgnoreProperties(ignoreUnknown = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(name = NotificationType.Constants.USER_REGISTERED, value = UserRegisteredNotificationContent.class)
})
@EqualsAndHashCode
@Getter
public class NotificationContent {
    private final NotificationType type;
}
