package pl.bronikowski.springchat.backendmain.videoroom.api;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import pl.bronikowski.springchat.backendmain.authserver.api.UserContextProvider;
import pl.bronikowski.springchat.backendmain.videoroom.internal.VideoRoomDestinations;
import pl.bronikowski.springchat.backendmain.videoroom.internal.VideoRoomService;
import pl.bronikowski.springchat.backendmain.websocket.api.StompMessagingTemplate;
import pl.bronikowski.springchat.backendmain.websocket.api.StompResponse;
import pl.bronikowski.springchat.backendmain.websocket.api.UserConnectionDetails;

@Validated
@Controller
@MessageMapping("video-rooms")
@RequiredArgsConstructor
public class StompVideoRoomController {
    private final VideoRoomService videoRoomService;
    private final StompMessagingTemplate template;

    @MessageMapping(".active.refresh")
    public void handleRefresh(Message<?> message) {
        var authResourceId = UserContextProvider.getAuthResourceId();
        var connectionDetails = UserConnectionDetails.fromMessage(message, authResourceId);
        var body = videoRoomService.extendUserSession(connectionDetails);
        var payload = StompResponse.message(body, message);
        var destination = VideoRoomDestinations.getTokenUserQueueDestination(body.videoRoomId().toString());
        template.convertAndSendToCurrentUser(authResourceId, destination, payload, message);
    }
}
