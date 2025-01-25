package pl.bronikowski.springchat.backendmain.videoroom.api.dto;

import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.VideoRoomAuthTokenDto;

import java.util.UUID;

public record VideoRoomSessionDetailsDto(
        UUID channelId,
        UUID videoRoomId,
        VideoRoomAuthTokenDto authToken,
        String videoRoomAccessToken
) {
}
