package pl.bronikowski.springchat.backendmain.videoroom.internal.activevideoroom;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@RedisHash("ActiveVideoRoom")
public final class ActiveVideoRoom {
    @Id
    private final UUID id;
    @Indexed
    private final UUID channelId;
    private final UUID videoRoomId;
    private final String videoRoomSecret;
    @Indexed
    private Instant expireAt;

}