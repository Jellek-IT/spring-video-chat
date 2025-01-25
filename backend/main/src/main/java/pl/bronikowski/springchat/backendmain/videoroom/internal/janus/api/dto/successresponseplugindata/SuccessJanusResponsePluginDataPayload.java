package pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.successresponseplugindata;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.JanusPlugin;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.successresponseplugindata.videoroom.VideoRoomSuccessJanusResponsePluginDataDataPayload;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record SuccessJanusResponsePluginDataPayload(
        @NotNull
        JanusPlugin plugin,
        @NotNull @Valid
        VideoRoomSuccessJanusResponsePluginDataDataPayload data
) {
    public SuccessJanusResponsePluginDataPayload(JanusPlugin plugin, VideoRoomSuccessJanusResponsePluginDataDataPayload data) {
        // for now only VIDEO_ROOM plugin is supported
        assert plugin == JanusPlugin.VIDEO_ROOM;
        this.plugin = plugin;
        this.data = data;
    }
}
