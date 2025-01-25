package pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum JanusVideoRoomResponseType {
    @JsonProperty(JanusVideoRoomResponseType.Constants.EVENT)
    EVENT,
    @JsonProperty(JanusVideoRoomResponseType.Constants.CREATED)
    CREATED,
    @JsonProperty(JanusVideoRoomResponseType.Constants.DESTROYED)
    DESTROYED,
    @JsonProperty(JanusVideoRoomResponseType.Constants.SUCCESS)
    SUCCESS;

    public static final class Constants {
        public static final String EVENT = "event";
        public static final String CREATED = "created";
        public static final String DESTROYED = "destroyed";
        public static final String SUCCESS = "success";
    }
}
