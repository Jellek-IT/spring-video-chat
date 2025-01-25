package pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AudioCodec {
    @JsonProperty("opus")
    OPUS,
    @JsonProperty("g722")
    G722,
    @JsonProperty("pcmu")
    PCMU,
    @JsonProperty("pcma")
    PCMA,
    @JsonProperty("isac32")
    ISAC32,
    @JsonProperty("isac16")
    ISAC16
}
