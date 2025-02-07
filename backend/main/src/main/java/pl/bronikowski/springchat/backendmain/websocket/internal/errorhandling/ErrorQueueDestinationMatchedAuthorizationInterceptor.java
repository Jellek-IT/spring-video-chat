package pl.bronikowski.springchat.backendmain.websocket.internal.errorhandling;

import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.stereotype.Component;
import pl.bronikowski.springchat.backendmain.websocket.api.DestinationMatchedAuthorizationInterceptor;
import pl.bronikowski.springchat.backendmain.websocket.api.StompDestinations;

import java.util.Map;

@Component
public class ErrorQueueDestinationMatchedAuthorizationInterceptor extends DestinationMatchedAuthorizationInterceptor {

    @Override
    protected String getTemplate() {
        return StompDestinations.USER_PREFIX + StompDestinations.ERRORS_QUEUE_DESTINATION;
    }

    @Override
    protected StompCommand getCommand() {
        return StompCommand.SUBSCRIBE;
    }

    @Override
    protected boolean hasAccess(Map<String, String> parameters) {
        return true;
    }
}
