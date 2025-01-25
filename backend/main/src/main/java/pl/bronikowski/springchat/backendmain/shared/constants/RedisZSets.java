package pl.bronikowski.springchat.backendmain.shared.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisZSets {
    public static final String ACTIVE_VIDEO_ROOM_EXPIRE_AT = "ActiveVideoRoom.expireAt";
    public static final String ACTIVE_VIDEO_ROOM_PARTICIPANT_EXPIRE_AT = "ActiveVideoRoomParticipant.expireAt";
}
