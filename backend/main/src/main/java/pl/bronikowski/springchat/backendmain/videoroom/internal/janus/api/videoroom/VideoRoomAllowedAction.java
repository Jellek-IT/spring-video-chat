package pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.videoroom;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum VideoRoomAllowedAction {
    @JsonProperty(VideoRoomAllowedAction.Constants.ENABLE)
    CREATE,
    @JsonProperty(VideoRoomAllowedAction.Constants.DISABLE)
    DISABLE,
    @JsonProperty(VideoRoomAllowedAction.Constants.ADD)
    ADD,
    @JsonProperty(VideoRoomAllowedAction.Constants.REMOVE)
    REMOVE;

    public static final class Constants {
        public static final String ENABLE = "enable";
        public static final String DISABLE = "disable";
        public static final String ADD = "add";
        public static final String REMOVE = "remove";
    }
}
