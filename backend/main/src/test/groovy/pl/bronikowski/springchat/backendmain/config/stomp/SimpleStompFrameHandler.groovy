package pl.bronikowski.springchat.backendmain.config.stomp

import groovy.transform.TupleConstructor
import org.springframework.lang.Nullable
import org.springframework.messaging.simp.stomp.StompFrameHandler
import org.springframework.messaging.simp.stomp.StompHeaders

import java.lang.reflect.Type

@TupleConstructor
class SimpleStompFrameHandler implements StompFrameHandler {
    Type payloadType
    Closure<Void> frameHandler

    @Override
    Type getPayloadType(StompHeaders headers) {
        return payloadType
    }

    @Override
    void handleFrame(StompHeaders headers, @Nullable Object payload) {
        frameHandler.call(headers, payload)
    }
}
