package pl.bronikowski.springchat.backendmain.videoroom.internal.authorization;

import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.stereotype.Component;
import pl.bronikowski.springchat.backendmain.websocket.api.DestinationMatchedAuthorizationInterceptor;

import java.util.Map;

@Component
public class VideoRoomRefreshDestinationMatchedAuthorizationInterceptor extends DestinationMatchedAuthorizationInterceptor {
    @Override
    protected String getTemplate() {
        return "/app/video-rooms.active.refresh";
    }

    @Override
    protected StompCommand getCommand() {
        return StompCommand.SEND;
    }

    /**
     * There is no need for authorization check, because video room client will refresh session only if active user
     * is present
     */
    @Override
    protected boolean hasAccess(Map<String, String> parameters) {
        return true;
    }
}
