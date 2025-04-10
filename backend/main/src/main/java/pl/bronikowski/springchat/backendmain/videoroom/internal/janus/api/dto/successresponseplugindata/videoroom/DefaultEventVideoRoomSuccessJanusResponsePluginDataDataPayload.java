package pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.successresponseplugindata.videoroom;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.JanusVideoRoomResponseType;

@Getter
@EqualsAndHashCode(callSuper = true)
public class DefaultEventVideoRoomSuccessJanusResponsePluginDataDataPayload
        extends VideoRoomSuccessJanusResponsePluginDataDataPayload {
    public DefaultEventVideoRoomSuccessJanusResponsePluginDataDataPayload() {
        super(JanusVideoRoomResponseType.EVENT);
    }
}
