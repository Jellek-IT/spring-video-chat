package pl.bronikowski.springchat.backendmain.websocket.internal;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.util.AntPathMatcher;
import pl.bronikowski.springchat.backendmain.authserver.internal.Roles;

@Configuration
public class WebSocketSecurityConfig {
    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        messages
                .nullDestMatcher().authenticated()
                .simpSubscribeDestMatchers("user.queue.errors").permitAll()
                .simpMessageDestMatchers("/app/channels.**").hasRole(Roles.MEMBER)
                .simpMessageDestMatchers("/app/video-rooms.**").hasRole(Roles.MEMBER)
                .simpSubscribeDestMatchers("/user/exchange/amq.direct/errors").hasRole(Roles.MEMBER)
                .simpSubscribeDestMatchers("/user/exchange/amq.direct/channels.**").hasRole(Roles.MEMBER)
                .simpSubscribeDestMatchers("/topic/channels.**").hasRole(Roles.MEMBER)
                .anyMessage().denyAll();
        return messages.build();
    }

    // without @EnableWebSocketSecurity MessageMatcherDelegatingAuthorizationManager.Builder is not defined
    @Bean
    MessageMatcherDelegatingAuthorizationManager.Builder messageAuthorizationManagerBuilder(ApplicationContext context) {
        return MessageMatcherDelegatingAuthorizationManager.builder().simpDestPathMatcher(() ->
                context.getBeanNamesForType(SimpAnnotationMethodMessageHandler.class).length > 0
                        ? context.getBean(SimpAnnotationMethodMessageHandler.class).getPathMatcher()
                        : new AntPathMatcher());
    }
}
