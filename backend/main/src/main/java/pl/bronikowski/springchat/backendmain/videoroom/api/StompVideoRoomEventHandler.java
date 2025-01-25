package pl.bronikowski.springchat.backendmain.videoroom.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;
import pl.bronikowski.springchat.backendmain.authserver.api.UserContextProvider;
import pl.bronikowski.springchat.backendmain.videoroom.internal.VideoRoomService;
import pl.bronikowski.springchat.backendmain.websocket.api.SessionDisconnectEventHandler;
import pl.bronikowski.springchat.backendmain.websocket.api.UnsubscribeEventHandler;
import pl.bronikowski.springchat.backendmain.websocket.api.UserConnectionDetails;
import pl.bronikowski.springchat.backendmain.websocket.api.UserSubscriptionDetails;

@Component
@RequiredArgsConstructor
public class StompVideoRoomEventHandler implements UnsubscribeEventHandler, SessionDisconnectEventHandler {
    private final VideoRoomService videoRoomService;

    @Override
    public void handleEvent(SessionDisconnectEvent event) {
        var userConnectionDetails = UserConnectionDetails.fromSubProtocolEvent(event);
        videoRoomService.leave(userConnectionDetails);
    }

    @Override
    public void handleEvent(SessionUnsubscribeEvent event) {
        var userSessionDetails = UserSubscriptionDetails.fromSubProtocolEvent(event);
        videoRoomService.leave(userSessionDetails);
    }
}
