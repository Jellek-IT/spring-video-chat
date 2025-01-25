package pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.successresponseplugindata.videoroom;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.JanusVideoRoomResponseType;

import java.util.List;
import java.util.UUID;

@Getter
@EqualsAndHashCode(callSuper = true)
public class AllowedSuccessVideoRoomSuccessJanusResponsePluginDataDataPayload
        extends VideoRoomSuccessJanusResponsePluginDataDataPayload {
    @NotNull
    private final UUID room;
    @NotNull
    private final List<String> allowed;

    public AllowedSuccessVideoRoomSuccessJanusResponsePluginDataDataPayload(UUID room, List<String> allowed) {
        super(JanusVideoRoomResponseType.SUCCESS);
        this.room = room;
        this.allowed = allowed;
    }
}
