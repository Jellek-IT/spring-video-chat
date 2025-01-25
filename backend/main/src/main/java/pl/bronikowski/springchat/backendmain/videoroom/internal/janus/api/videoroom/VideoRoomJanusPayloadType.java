package pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.videoroom;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum VideoRoomJanusPayloadType {
    @JsonProperty(VideoRoomJanusPayloadType.Constants.CREATE)
    CREATE,
    @JsonProperty(VideoRoomJanusPayloadType.Constants.ALLOWED)
    ALLOWED,
    @JsonProperty(VideoRoomJanusPayloadType.Constants.DESTROY)
    DESTROY;

    public static final class Constants {
        public static final String CREATE = "create";
        public static final String ALLOWED = "allowed";
        public static final String DESTROY = "destroy";
    }


}
