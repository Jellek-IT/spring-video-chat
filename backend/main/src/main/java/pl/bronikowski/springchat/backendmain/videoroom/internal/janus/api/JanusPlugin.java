package pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JanusPlugin {
    VIDEO_ROOM("janus.plugin.videoroom");
    @JsonValue
    private final String bundleName;
}
