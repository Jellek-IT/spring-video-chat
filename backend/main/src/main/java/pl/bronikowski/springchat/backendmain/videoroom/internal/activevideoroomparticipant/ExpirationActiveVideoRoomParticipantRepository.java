package pl.bronikowski.springchat.backendmain.videoroom.internal.activevideoroomparticipant;

import pl.bronikowski.springchat.backendmain.videoroom.internal.activevideoroom.ActiveVideoRoom;

import java.time.Instant;

public interface ExpirationActiveVideoRoomParticipantRepository {
    ActiveVideoRoomParticipant saveWithExpireAtUpdate(ActiveVideoRoomParticipant participant);

    void deleteWithExpireAtByRoom(ActiveVideoRoom room);

    void deleteWithExpireAt(ActiveVideoRoomParticipant participant);

    Iterable<ActiveVideoRoomParticipant> findExpired(Instant before);
}
