package pl.bronikowski.springchat.backendmain.websocket.internal;

import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AuthenticationChannelInterceptor implements ChannelInterceptor {
    private static final Pattern AUTHORIZATION_PATTERN = Pattern.compile("^bearer (?<token>[a-z0-9-._~+/]+=*)$", Pattern.CASE_INSENSITIVE);
    private static final String BEARER_TOKEN_HEADER_NAME = "Authorization";
    private static final String BEARER_TOKEN_PREFIX = "bearer";
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    public AuthenticationChannelInterceptor(JwtDecoder jwtDecoder,
                                            JwtAuthenticationConverter jwtAuthenticationConverter) {
        this.jwtAuthenticationProvider = new JwtAuthenticationProvider(jwtDecoder);
        this.jwtAuthenticationProvider.setJwtAuthenticationConverter(jwtAuthenticationConverter);
    }

    @Override
    @NonNull
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        var accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            this.authenticate(accessor);
        }
        return message;
    }

    private void authenticate(StompHeaderAccessor accessor) {
        try {
            var authentication = this.resolveToken(accessor);
            if (authentication == null) {
                return;
            }
            var principal = jwtAuthenticationProvider.authenticate(authentication);
            accessor.setUser(principal);
        } catch (AuthenticationException ignored) {
        }
    }

    private Authentication resolveToken(StompHeaderAccessor accessor) {
        var header = accessor.getNativeHeader(BEARER_TOKEN_HEADER_NAME);
        var authorization = header != null && !header.isEmpty() ? header.getFirst() : null;
        if (!StringUtils.startsWithIgnoreCase(authorization, BEARER_TOKEN_PREFIX)) {
            return null;
        } else {
            Matcher matcher = AUTHORIZATION_PATTERN.matcher(authorization);
            return matcher.matches()
                    ? new BearerTokenAuthenticationToken(matcher.group("token"))
                    : null;
        }
    }
}
