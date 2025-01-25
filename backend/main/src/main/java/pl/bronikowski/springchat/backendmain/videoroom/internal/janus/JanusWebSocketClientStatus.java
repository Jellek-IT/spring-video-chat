package pl.bronikowski.springchat.backendmain.videoroom.internal.janus;

import java.util.Set;

public enum JanusWebSocketClientStatus {
    CONNECTING,
    CONNECTED,
    CREATING_SESSION,
    CREATING_HANDLE,
    DISCONNECTING_RECONNECT,
    DISCONNECTED_RECONNECT,
    DISCONNECTING,
    DISCONNECTED;

    public JanusWebSocketClientStatus getDisconnectedStatus() {
        return NO_RECONNECT_STATUSES.contains(this)
                ? DISCONNECTED
                : DISCONNECTED_RECONNECT;
    }

    public boolean canReconnect() {
        return !NO_RECONNECT_STATUSES.contains(this);
    }

    private static final Set<JanusWebSocketClientStatus> NO_RECONNECT_STATUSES = Set.of(DISCONNECTING, DISCONNECTED);
}
