package pl.bronikowski.springchat.backendmain.videoroom.internal.activevideoroom;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import pl.bronikowski.springchat.backendmain.shared.constants.RedisZSets;
import pl.bronikowski.springchat.backendmain.videoroom.internal.activevideoroomparticipant.ActiveVideoRoomParticipantRepository;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
public class ExpirationActiveVideoRoomRepositoryImpl implements ExpirationActiveVideoRoomRepository {
    private final StringRedisTemplate redisTemplate;
    private final BasicActiveVideoRoomRepository basicActiveVideoRoomRepository;
    private final ActiveVideoRoomParticipantRepository activeVideoRoomParticipantRepository;

    @Override
    public ActiveVideoRoom saveWithExpireAtUpdate(ActiveVideoRoom room) {
        redisTemplate.opsForZSet()
                .add(RedisZSets.ACTIVE_VIDEO_ROOM_EXPIRE_AT, room.getId().toString(),
                        room.getExpireAt().toEpochMilli());
        return basicActiveVideoRoomRepository.save(room);
    }

    @Override
    public void deleteWithExpireAt(ActiveVideoRoom room) {
        activeVideoRoomParticipantRepository.deleteWithExpireAtByRoom(room);
        redisTemplate.opsForZSet().remove(RedisZSets.ACTIVE_VIDEO_ROOM_EXPIRE_AT, room.getId().toString());
        basicActiveVideoRoomRepository.delete(room);
    }

    @Override
    public Iterable<ActiveVideoRoom> findExpired(Instant before) {
        var ids = redisTemplate.opsForZSet()
                .rangeWithScores(RedisZSets.ACTIVE_VIDEO_ROOM_EXPIRE_AT, Long.MIN_VALUE, before.toEpochMilli())
                .stream()
                .map(result -> UUID.fromString(result.getValue()))
                .toList();
        return basicActiveVideoRoomRepository.findAllById(ids);
    }
}
