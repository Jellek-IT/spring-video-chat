package pl.bronikowski.springchat.backendmain.videoroom.internal.activevideoroom;

import java.time.Instant;

public interface ExpirationActiveVideoRoomRepository {
    ActiveVideoRoom saveWithExpireAtUpdate(ActiveVideoRoom room);

    void deleteWithExpireAt(ActiveVideoRoom room);

    Iterable<ActiveVideoRoom> findExpired(Instant before);
}
