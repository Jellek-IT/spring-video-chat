package pl.bronikowski.springchat.backendmain.notification.internal.kafka.dto;

import pl.bronikowski.springchat.backendmain.notification.internal.kafka.dto.content.NotificationContent;

import java.util.UUID;

public record NotificationDto(
        NotificationUserDto user,
        UUID remoteNotificationId,
        NotificationContent content
) {
}
