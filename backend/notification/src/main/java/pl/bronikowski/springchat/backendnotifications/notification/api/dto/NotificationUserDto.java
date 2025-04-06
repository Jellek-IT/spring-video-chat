package pl.bronikowski.springchat.backendnotifications.notification.api.dto;

import java.util.UUID;

public record NotificationUserDto(
        UUID id,
        String email
) {
}
