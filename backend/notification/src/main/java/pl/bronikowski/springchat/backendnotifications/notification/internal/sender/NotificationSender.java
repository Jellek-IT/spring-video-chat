package pl.bronikowski.springchat.backendnotifications.notification.internal.sender;

import pl.bronikowski.springchat.backendnotifications.notification.internal.NotificationChannel;
import pl.bronikowski.springchat.backendnotifications.notification.internal.Notification;

import java.util.concurrent.CompletableFuture;

public interface NotificationSender {
    CompletableFuture<Void> send(Notification notification);

    boolean isApplicable(NotificationChannel channel);
}
