package pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.successresponseplugindata.videoroom;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.JanusVideoRoomResponseType;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ErrorEventVideoRoomSuccessJanusResponsePluginDataDataPayload
        extends VideoRoomSuccessJanusResponsePluginDataDataPayload {
    @NotNull
    private final Integer errorCode;
    @NotNull
    private final String error;

    public ErrorEventVideoRoomSuccessJanusResponsePluginDataDataPayload(Integer errorCode, String error) {
        super(JanusVideoRoomResponseType.EVENT);
        this.errorCode = errorCode;
        this.error = error;
    }
}
