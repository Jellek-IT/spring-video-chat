package pl.bronikowski.springchat.backendmain.websocket.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.SpringAuthorizationEventPublisher;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.context.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
//https://docs.spring.io/spring-framework/reference/web/websocket/stomp/authentication-token-based.html
//ordered ahead of Spring Security’s but spring security usage was configured manually to disable csrf
//@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final StompBrokerProperties stompBrokerProperties;
    private final AuthenticationChannelInterceptor authenticationChannelInterceptor;
    private final DestinationAuthorizationInterceptor destinationAuthorizationInterceptor;
    private final ApplicationContext applicationContext;
    private final AuthorizationManager<Message<?>> authorizationManager;
    private final StompErrorHandler stompErrorHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.setErrorHandler(stompErrorHandler)
                .addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setPathMatcher(new AntPathMatcher("."))
                .setUserDestinationPrefix("/user")
                .setApplicationDestinationPrefixes("/app")
                .enableStompBrokerRelay("/topic", "/queue")
                .setUserDestinationBroadcast("/topic/server.main.unresolved-user")
                .setUserRegistryBroadcast("/topic/server.main.user-registry")
                .setRelayHost(stompBrokerProperties.relayHost())
                .setRelayPort(stompBrokerProperties.relayPort())
                .setClientLogin(stompBrokerProperties.clientLogin())
                .setClientPasscode(stompBrokerProperties.clientPasscode());
    }

    @Override
    public Integer getPhase() {
        return 0;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new AuthenticationPrincipalArgumentResolver());
    }

    @Override
    //https://docs.spring.io/spring-security/reference/servlet/integrations/websocket.html#websocket-sameorigin-disable
    public void configureClientInboundChannel(ChannelRegistration registration) {
        var authorizationInterceptor = new AuthorizationChannelInterceptor(authorizationManager);
        var publisher = new SpringAuthorizationEventPublisher(applicationContext);
        authorizationInterceptor.setAuthorizationEventPublisher(publisher);
        registration.interceptors(
                authenticationChannelInterceptor,
                new SecurityContextChannelInterceptor(),
                authorizationInterceptor,
                destinationAuthorizationInterceptor
        );
    }
}
