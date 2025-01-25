package pl.bronikowski.springchat.backendmain.videoroom.internal;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VideoRoomDestinations {
    public static String getTokenUserQueueDestination(String id) {
        return "/queue/channels." + id + ".video-room.token";
    }
}
