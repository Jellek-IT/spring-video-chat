package pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.JanusRequestType;

@Getter
@EqualsAndHashCode(callSuper = true)
public class KeepAliveRequestJacksonPayload extends JanusRequestPayload {
    @NotNull
    private final Long sessionId;

    public KeepAliveRequestJacksonPayload(@NotNull String transaction, @NotNull Long sessionId) {
        super(JanusRequestType.KEEP_ALIVE, transaction);
        this.sessionId = sessionId;
    }
}
