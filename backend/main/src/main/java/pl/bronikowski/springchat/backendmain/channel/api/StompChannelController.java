package pl.bronikowski.springchat.backendmain.channel.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import pl.bronikowski.springchat.backendmain.authserver.api.UserContextProvider;
import pl.bronikowski.springchat.backendmain.channel.api.dto.message.ChannelMessageBasicsDto;
import pl.bronikowski.springchat.backendmain.channel.api.dto.message.CreateChannelMessagePayload;
import pl.bronikowski.springchat.backendmain.channel.internal.message.ChannelMessageService;
import pl.bronikowski.springchat.backendmain.exception.ExceptionMapper;
import pl.bronikowski.springchat.backendmain.websocket.api.StompResponse;

import java.util.UUID;

@Validated
@Controller
@MessageMapping("channels")
@RequiredArgsConstructor
public class StompChannelController {
    private final ChannelMessageService channelMessageService;
    private final ExceptionMapper exceptionMapper;

    @MessageMapping(".{id}.create-message")
    @SendTo("/topic/channels.{id}.message")
    public StompResponse<ChannelMessageBasicsDto> handleChannelCreateMessage(@DestinationVariable UUID id,
                                                                             @Valid @Payload CreateChannelMessagePayload payload,
                                                                             StompHeaderAccessor stompHeaderAccessor) {
        var authResourceId = UserContextProvider.getAuthResourceId();
        var response = channelMessageService.create(id, payload, authResourceId);
        return StompResponse.message(response, stompHeaderAccessor);
    }
}
