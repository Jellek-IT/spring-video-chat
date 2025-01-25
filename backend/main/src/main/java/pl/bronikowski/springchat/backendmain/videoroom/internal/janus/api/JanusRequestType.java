package pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum JanusRequestType {
    @JsonProperty(JanusRequestType.Constants.CREATE)
    CREATE,
    @JsonProperty(JanusRequestType.Constants.ATTACH)
    ATTACH,
    @JsonProperty(JanusRequestType.Constants.KEEP_ALIVE)
    KEEP_ALIVE,
    @JsonProperty(JanusRequestType.Constants.MESSAGE)
    MESSAGE;

    public static final class Constants {
        public static final String CREATE = "create";
        public static final String ATTACH = "attach";
        public static final String KEEP_ALIVE = "keepalive";
        public static final String MESSAGE = "message";
    }
}
