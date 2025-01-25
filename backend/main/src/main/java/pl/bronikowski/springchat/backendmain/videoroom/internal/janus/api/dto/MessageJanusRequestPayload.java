package pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.JanusRequestType;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.messagerequestbody.MessageJanusRequestBodyPayload;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.messagerequestbody.videoroom.VideoRoomMessageJanusRequestBodyPayload;

@Getter
@EqualsAndHashCode(callSuper = true)
public class MessageJanusRequestPayload extends JanusRequestPayload {
    @NotNull
    private final Long sessionId;
    @NotNull
    private final Long handleId;
    @NotNull
    private final MessageJanusRequestBodyPayload body;

    public MessageJanusRequestPayload(@NotNull String transaction, @NotNull Long sessionId, @NotNull Long handleId,
                                      @NotNull VideoRoomMessageJanusRequestBodyPayload body) {
        super(JanusRequestType.MESSAGE, transaction);
        this.sessionId = sessionId;
        this.handleId = handleId;
        this.body = body;
    }
}
