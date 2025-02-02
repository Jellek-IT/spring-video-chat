package pl.bronikowski.springchat.backendmain.videoroom.internal;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VideoRoomDestinations {
    public static String getTokenUserQueueDestination(String id) {
        return "/exchange/amq.direct/channels." + id + ".video-room.token";
    }
}
