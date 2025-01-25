package pl.bronikowski.springchat.backendmain.websocket.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ExecutorChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import pl.bronikowski.springchat.backendmain.exception.AppInternalServerErrorException;
import pl.bronikowski.springchat.backendmain.websocket.api.DestinationMatchedAuthorizationInterceptor;
import pl.bronikowski.springchat.backendmain.websocket.api.StompAccessException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DestinationAuthorizationInterceptor implements ChannelInterceptor {
    private final List<DestinationMatchedAuthorizationInterceptor> destinationMatchedAuthorizationInterceptors;

    @Override
    @NonNull
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {

        var accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || accessor.getCommand() == null
                || !StompConstants.CLIENT_DESTINATION_COMMANDS.contains(accessor.getCommand())) {
            return message;
        }
        var haAccess = destinationMatchedAuthorizationInterceptors.stream()
                .filter(interceptor -> interceptor.matches(message))
                .findAny()
                .map(interceptor -> interceptor.hasAccess(message))
                .orElseThrow(() -> new AppInternalServerErrorException(
                        "There should exist at least one interceptor for each possible destination"));
        if (!haAccess) {
            throw new StompAccessException(accessor.getDestination());
        }
        return message;
    }
}
