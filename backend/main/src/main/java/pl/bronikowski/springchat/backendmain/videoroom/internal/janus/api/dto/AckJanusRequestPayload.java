package pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.JanusResponseType;

@Getter
@EqualsAndHashCode(callSuper = true)
public class AckJanusRequestPayload extends JanusResponsePayload {
    public AckJanusRequestPayload(@JsonProperty("transaction") String transaction) {
        super(JanusResponseType.ACK, transaction);
    }
}
