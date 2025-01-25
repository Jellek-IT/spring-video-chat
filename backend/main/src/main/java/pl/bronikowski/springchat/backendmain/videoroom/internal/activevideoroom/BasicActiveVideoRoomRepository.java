package pl.bronikowski.springchat.backendmain.videoroom.internal.activevideoroom;

import org.springframework.data.repository.CrudRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BasicActiveVideoRoomRepository extends CrudRepository<ActiveVideoRoom, UUID> {
    List<ActiveVideoRoom> findByChannelId(UUID channelId);

    default Optional<ActiveVideoRoom> findByChannelIdAndExpireAtAfter(UUID channelId, Instant after) {
        return findByChannelId(channelId).stream()
                .filter(participant -> participant.getExpireAt().isAfter(after))
                .findFirst();
    }
}
