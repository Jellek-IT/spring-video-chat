package pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.successresponseplugindata.videoroom;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.JanusVideoRoomResponseType;

import java.util.UUID;

@Getter
@EqualsAndHashCode(callSuper = true)
public class DestroyedVideoRoomSuccessJanusResponsePluginDataDataPayload
        extends VideoRoomSuccessJanusResponsePluginDataDataPayload {
    @NotNull
    private final UUID room;

    public DestroyedVideoRoomSuccessJanusResponsePluginDataDataPayload(@JsonProperty("room") UUID room) {
        super(JanusVideoRoomResponseType.DESTROYED);
        this.room = room;
    }
}
