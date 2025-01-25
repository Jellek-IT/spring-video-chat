package pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.successresponseplugindata.videoroom;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.JanusVideoRoomResponseType;

import java.util.UUID;

@Getter
@EqualsAndHashCode(callSuper = true)
public class CreatedVideoRoomSuccessJanusResponsePluginDataDataPayload
        extends VideoRoomSuccessJanusResponsePluginDataDataPayload {
    @NotNull
    private final UUID room;
    @NotNull
    private final Boolean permanent;

    public CreatedVideoRoomSuccessJanusResponsePluginDataDataPayload(UUID room, Boolean permanent) {
        super(JanusVideoRoomResponseType.CREATED);
        this.room = room;
        this.permanent = permanent;
    }
}
