package pl.bronikowski.springchat.backendmain.config.stomp

import groovy.transform.TupleConstructor
import org.springframework.lang.Nullable
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter

import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicReference

@TupleConstructor
abstract class AbstractTestStompSessionHandler extends StompSessionHandlerAdapter {

    AtomicReference<Throwable> failure;

    @Override
    Type getPayloadType(StompHeaders headers) {
        return Map.class
    }

    @Override
    void handleFrame(StompHeaders headers, @Nullable Object payload) {
        failure.set(new Exception(headers.toString()));
    }

    @Override
    void handleException(StompSession session, @Nullable StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        failure.set(exception);
    }

    @Override
    void handleTransportError(StompSession session, Throwable exception) {
        failure.set(exception);
    }
}
