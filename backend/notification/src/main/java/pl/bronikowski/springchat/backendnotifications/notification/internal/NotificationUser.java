package pl.bronikowski.springchat.backendnotifications.notification.internal;

import java.util.UUID;

public record NotificationUser(
        UUID id,
        String email
) {
}
