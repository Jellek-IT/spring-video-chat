package pl.bronikowski.springchat.backendmain.videoroom.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import pl.bronikowski.springchat.backendmain.exception.ExceptionMapper;
import pl.bronikowski.springchat.backendmain.videoroom.internal.VideoRoomDestinations;
import pl.bronikowski.springchat.backendmain.videoroom.internal.VideoRoomService;
import pl.bronikowski.springchat.backendmain.websocket.api.DestinationMatchedSubscribeEventHandler;
import pl.bronikowski.springchat.backendmain.websocket.api.StompMessagingTemplate;
import pl.bronikowski.springchat.backendmain.websocket.api.StompResponse;
import pl.bronikowski.springchat.backendmain.websocket.api.UserSubscriptionDetails;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChannelVideoRoomTokenUserQueueDestinationMatchedSubscribeEventHandler extends DestinationMatchedSubscribeEventHandler {

    private final StompMessagingTemplate template;
    private final VideoRoomService videoRoomService;
    private final ExceptionMapper exceptionMapper;

    @Override
    protected String getTemplate() {
        return "/user" + VideoRoomDestinations.getTokenUserQueueDestination("{id}");
    }

    @Override
    protected void handleEvent(Map<String, String> parameters, SessionSubscribeEvent event) {
        var message = event.getMessage();
        var authResourceId = event.getUser().getName();
        var id = UUID.fromString(parameters.get("id"));
        var userSessionDetails = UserSubscriptionDetails.fromSubProtocolEvent(event);
        try {
            var body = videoRoomService.join(id, userSessionDetails);
            var payload = StompResponse.message(body, message);
            var destination = VideoRoomDestinations.getTokenUserQueueDestination(id.toString());
            template.convertAndSendToCurrentUser(authResourceId, destination, payload, message);
        } catch (Exception e) {
            template.convertAndSendErrorToCurrentUser(authResourceId, e, message);
        }
    }
}
