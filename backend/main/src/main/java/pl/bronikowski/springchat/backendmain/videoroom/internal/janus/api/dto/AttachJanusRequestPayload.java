package pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.JanusPlugin;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.JanusRequestType;

@Getter
@EqualsAndHashCode(callSuper = true)
public class AttachJanusRequestPayload extends JanusRequestPayload {
    @NotNull
    private final Long sessionId;
    @NotNull
    private final JanusPlugin plugin;

    public AttachJanusRequestPayload(@NotNull String transaction, @NotNull Long sessionId, @NotNull JanusPlugin plugin) {
        super(JanusRequestType.ATTACH, transaction);
        this.sessionId = sessionId;
        this.plugin = plugin;
    }
}
