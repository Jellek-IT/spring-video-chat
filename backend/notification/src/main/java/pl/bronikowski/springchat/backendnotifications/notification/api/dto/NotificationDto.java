package pl.bronikowski.springchat.backendnotifications.notification.api.dto;

import pl.bronikowski.springchat.backendnotifications.notification.api.dto.content.NotificationContent;

import java.util.UUID;

public record NotificationDto(
        NotificationUserDto user,
        UUID remoteNotificationId,
        NotificationContent content
) {
}
