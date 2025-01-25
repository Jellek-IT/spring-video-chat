package pl.bronikowski.springchat.backendmain.videoroom.internal.activevideoroomparticipant;

import org.springframework.data.repository.CrudRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BasicActiveVideoRoomParticipantRepository extends CrudRepository<ActiveVideoRoomParticipant, UUID> {
    List<ActiveVideoRoomParticipant> findByAuthResourceId(String authResourceId);

    default boolean existsByAuthResourceIdAndExpireAtAfter(String authResourceId, Instant after) {
        return findByAuthResourceId(authResourceId).stream()
                .anyMatch(participant -> participant.getExpireAt().isAfter(after));
    }

    List<ActiveVideoRoomParticipant> findByAuthResourceIdAndSessionId(String authResourceId, String sessionId);

    default Optional<ActiveVideoRoomParticipant> findByAuthResourceIdAndSessionIdAndExpireAtAfter(
            String authResourceId, String sessionId, Instant after) {
        return findByAuthResourceIdAndSessionId(authResourceId, sessionId).stream()
                .filter(participant -> participant.getExpireAt().isAfter(after))
                .findFirst();
    }

    List<ActiveVideoRoomParticipant> findByAuthResourceIdAndSessionIdAndSubscriptionId(
            String authResourceId, String sessionId, String subscriptionId);

    default List<ActiveVideoRoomParticipant> findByAuthResourceIdAndSessionIdAndSubscriptionIdAndExpireAtAfter(
            String authResourceId, String sessionId, String subscriptionId, Instant after) {
        return findByAuthResourceIdAndSessionIdAndSubscriptionId(authResourceId, sessionId, subscriptionId).stream()
                .filter(participant -> participant.getExpireAt().isAfter(after))
                .toList();
    }

    List<ActiveVideoRoomParticipant> findAllByRoomId(UUID roomId);
}
