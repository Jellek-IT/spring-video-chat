package pl.bronikowski.springchat.backendmain.videoroom.internal.janus;

import java.time.Instant;

public record VideoRoomAuthTokenDto(
        String value,
        Instant expireAt
) {
}
