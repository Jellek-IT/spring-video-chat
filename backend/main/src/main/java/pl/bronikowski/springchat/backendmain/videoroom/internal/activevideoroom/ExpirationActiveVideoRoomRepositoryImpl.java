package pl.bronikowski.springchat.backendmain.videoroom.internal.activevideoroom;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import pl.bronikowski.springchat.backendmain.shared.constants.RedisZSets;
import pl.bronikowski.springchat.backendmain.videoroom.internal.activevideoroomparticipant.ActiveVideoRoomParticipantRepository;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

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
                .rangeByScore(RedisZSets.ACTIVE_VIDEO_ROOM_EXPIRE_AT, Long.MIN_VALUE, before.toEpochMilli())
                .stream()
                .map(UUID::fromString)
                .collect(Collectors.toSet());
        return basicActiveVideoRoomRepository.findAllById(ids);
    }
}
