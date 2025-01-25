package pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum VideoCodec {
    @JsonProperty("vp8")
    VP8,
    @JsonProperty("vp9")
    VP9,
    @JsonProperty("h264")
    H264,
    @JsonProperty("av1")
    AV1,
    @JsonProperty("h265")
    H265
}
