package pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.JanusRequestType;

@Getter
@EqualsAndHashCode(callSuper = true)
public class CreateJanusRequestPayload extends JanusRequestPayload {
    public CreateJanusRequestPayload(String transaction) {
        super(JanusRequestType.CREATE, transaction);
    }
}
