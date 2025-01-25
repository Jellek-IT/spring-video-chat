package pl.bronikowski.springchat.backendmain.videoroom.internal.janus;

import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.JanusResponsePayload;

public interface JanusTextMessageHandler {
    void run(JanusResponsePayload payload);
}
