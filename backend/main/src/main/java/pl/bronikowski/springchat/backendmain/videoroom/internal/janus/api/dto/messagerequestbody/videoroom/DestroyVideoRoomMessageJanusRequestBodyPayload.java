package pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.messagerequestbody.videoroom;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.videoroom.VideoRoomJanusPayloadType;

import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class DestroyVideoRoomMessageJanusRequestBodyPayload extends VideoRoomMessageJanusRequestBodyPayload {
    private UUID room;
    private String secret;
    private Boolean permanent;

    public DestroyVideoRoomMessageJanusRequestBodyPayload() {
        super(VideoRoomJanusPayloadType.DESTROY);
    }
}
