package pl.bronikowski.springchat.backendmain.videoroom.internal.activevideoroomparticipant;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@RedisHash("ActiveVideoRoomParticipant")
public final class ActiveVideoRoomParticipant {
    @Id
    private final UUID id;
    @Indexed
    private final String authResourceId;
    @Indexed
    private final String sessionId;
    @Indexed
    private final String subscriptionId;
    private final String videoRoomAccessToken;
    private Instant expireAt;
    @Indexed
    private final UUID roomId;
}
