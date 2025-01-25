package pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.messagerequestbody.videoroom;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.messagerequestbody.MessageJanusRequestBodyPayload;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.videoroom.VideoRoomJanusPayloadType;

@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class VideoRoomMessageJanusRequestBodyPayload extends MessageJanusRequestBodyPayload {
    private final VideoRoomJanusPayloadType request;

    protected VideoRoomMessageJanusRequestBodyPayload(VideoRoomJanusPayloadType request) {
        super();
        this.request = request;
    }
}
