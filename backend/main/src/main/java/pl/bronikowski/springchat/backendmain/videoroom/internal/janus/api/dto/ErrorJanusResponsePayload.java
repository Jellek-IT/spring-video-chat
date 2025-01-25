package pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.bronikowski.springchat.backendmain.videoroom.api.exception.VideoRoomClientException;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.JanusResponseType;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ErrorJanusResponsePayload extends JanusResponsePayload {
    @NotNull
    @Valid
    private final ErrorJanusPayloadError error;

    protected ErrorJanusResponsePayload(String transaction, ErrorJanusPayloadError error) {
        super(JanusResponseType.ERROR, transaction);
        this.error = error;
    }

    @JsonIgnore
    public VideoRoomClientException toException() {
        return new VideoRoomClientException(error.reason);
    }

    public record ErrorJanusPayloadError(
            @NotNull
            Integer code,
            @NotNull
            String reason
    ) {

    }
}
