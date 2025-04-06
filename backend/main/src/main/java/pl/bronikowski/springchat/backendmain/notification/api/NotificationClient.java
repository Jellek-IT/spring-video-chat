package pl.bronikowski.springchat.backendmain.notification.api;

import pl.bronikowski.springchat.backendmain.user.internal.User;

public interface NotificationClient {
    void sendUserRegisteredNotification(User user, String token);
}
