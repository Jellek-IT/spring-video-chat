package pl.bronikowski.springchat.backendmain.websocket.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StompDestinations {
    public static final String ERRORS_QUEUE_DESTINATION = "/exchange/amq.direct/errors";
}
