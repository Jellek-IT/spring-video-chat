package pl.bronikowski.springchat.backendmain.websocket.internal;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.messaging.simp.stomp.StompCommand;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StompConstants {
    public static final String APP_TRANSACTION_HEADER = "X-Transaction-Id";
    public static final Set<StompCommand> CLIENT_DESTINATION_COMMANDS = Set.of(StompCommand.SUBSCRIBE,
            StompCommand.SEND);
    public static final String MESSAGE_TYPE_HEADER = "simpMessageType";
}
