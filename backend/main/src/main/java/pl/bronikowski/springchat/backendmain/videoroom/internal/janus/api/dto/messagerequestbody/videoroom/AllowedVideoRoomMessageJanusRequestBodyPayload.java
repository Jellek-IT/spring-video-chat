package pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.messagerequestbody.videoroom;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.videoroom.VideoRoomAllowedAction;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.videoroom.VideoRoomJanusPayloadType;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class AllowedVideoRoomMessageJanusRequestBodyPayload extends VideoRoomMessageJanusRequestBodyPayload {
    private String secret;
    private VideoRoomAllowedAction action;
    private UUID room;
    private List<String> allowed;
    public AllowedVideoRoomMessageJanusRequestBodyPayload() {
        super(VideoRoomJanusPayloadType.ALLOWED);
    }
}
