package pl.bronikowski.springchat.backendmain.videoroom.internal.activevideoroomparticipant;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import pl.bronikowski.springchat.backendmain.shared.constants.RedisZSets;
import pl.bronikowski.springchat.backendmain.videoroom.internal.activevideoroom.ActiveVideoRoom;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
public class ExpirationActiveVideoRoomParticipantRepositoryImpl implements ExpirationActiveVideoRoomParticipantRepository {
    private final StringRedisTemplate redisTemplate;
    private final BasicActiveVideoRoomParticipantRepository basicActiveVideoRoomParticipantRepository;

    @Override
    public ActiveVideoRoomParticipant saveWithExpireAtUpdate(ActiveVideoRoomParticipant participant) {
        redisTemplate.opsForZSet()
                .add(RedisZSets.ACTIVE_VIDEO_ROOM_PARTICIPANT_EXPIRE_AT, participant.getId().toString(),
                        participant.getExpireAt().toEpochMilli());
        return basicActiveVideoRoomParticipantRepository.save(participant);
    }

    @Override
    public void deleteWithExpireAtByRoom(ActiveVideoRoom room) {
        var participants = basicActiveVideoRoomParticipantRepository.findAllByRoomId(room.getId());
        participants.forEach(this::removeFromZSet);
        basicActiveVideoRoomParticipantRepository.deleteAll(participants);
    }

    @Override
    public void deleteWithExpireAt(ActiveVideoRoomParticipant participant) {
        removeFromZSet(participant);
        basicActiveVideoRoomParticipantRepository.delete(participant);
    }

    private void removeFromZSet(ActiveVideoRoomParticipant participant) {
        redisTemplate.opsForZSet()
                .remove(RedisZSets.ACTIVE_VIDEO_ROOM_PARTICIPANT_EXPIRE_AT, participant.getId().toString());
    }

    @Override
    public Iterable<ActiveVideoRoomParticipant> findExpired(Instant before) {
        var ids = redisTemplate.opsForZSet()
                .rangeWithScores(RedisZSets.ACTIVE_VIDEO_ROOM_PARTICIPANT_EXPIRE_AT, Long.MIN_VALUE, before.toEpochMilli())
                .stream()
                .map(result -> UUID.fromString(result.getValue()))
                .toList();
        return basicActiveVideoRoomParticipantRepository.findAllById(ids);
    }
}
