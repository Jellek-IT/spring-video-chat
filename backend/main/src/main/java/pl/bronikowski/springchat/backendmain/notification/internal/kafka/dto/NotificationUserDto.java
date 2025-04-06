package pl.bronikowski.springchat.backendmain.notification.internal.kafka.dto;

import java.util.UUID;

public record NotificationUserDto(
        UUID id,
        String email
) {
}
