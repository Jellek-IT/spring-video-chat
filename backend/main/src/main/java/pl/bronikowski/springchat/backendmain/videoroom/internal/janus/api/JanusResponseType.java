package pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum JanusResponseType {
    @JsonProperty(Constants.SUCCESS)
    SUCCESS,
    @JsonProperty(Constants.ERROR)
    ERROR,
    @JsonProperty(Constants.ACK)
    ACK;

    public static final class Constants {
        public static final String SUCCESS = "success";
        public static final String ERROR = "error";
        public static final String ACK = "ack";
    }
}
