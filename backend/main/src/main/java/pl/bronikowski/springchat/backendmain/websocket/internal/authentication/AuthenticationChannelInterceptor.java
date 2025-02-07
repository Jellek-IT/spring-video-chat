package pl.bronikowski.springchat.backendmain.websocket.internal.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import pl.bronikowski.springchat.backendmain.exception.AppBadRequestException;

@Component
@RequiredArgsConstructor
public class AuthenticationChannelInterceptor implements ChannelInterceptor {
    private final StompAuthenticationProvider stompAuthenticationProvider;

    @Override
    @NonNull
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        var accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            throw new AppBadRequestException("message headers was null");
        }
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            stompAuthenticationProvider.authenticate(accessor);
        }
        return message;
    }
}
