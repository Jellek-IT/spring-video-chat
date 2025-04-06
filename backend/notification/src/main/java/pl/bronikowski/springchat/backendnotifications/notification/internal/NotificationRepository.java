package pl.bronikowski.springchat.backendnotifications.notification.internal;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    boolean existsByRemoteNotificationId(UUID remoteNotificationId);
}
