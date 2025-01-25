package pl.bronikowski.springchat.backendmain.websocket.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;
import pl.bronikowski.springchat.backendmain.exception.AppApplicationException;
import pl.bronikowski.springchat.backendmain.exception.ExceptionMapper;
import pl.bronikowski.springchat.backendmain.websocket.api.StompResponse;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompErrorHandler extends StompSubProtocolErrorHandler {
    private static final byte[] EMPTY_PAYLOAD = new byte[0];
    private final ObjectMapper objectMapper;
    private final ExceptionMapper exceptionMapper;

    // StompCommand.ERROR is causing connection to close by standard and this behaviour cannot be simply changed.
    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable e) {
        var handledException = e instanceof MessageDeliveryException && e.getCause() != null ? e.getCause() : e;
        var clientHeaderAccessor = clientMessage != null
                ? MessageHeaderAccessor.getAccessor(clientMessage, StompHeaderAccessor.class)
                : null;
        var payload = getPayload(handledException, clientHeaderAccessor);
        var command = this.shouldSendAsMessage(handledException) && payload.length > 0
                ? StompCommand.MESSAGE
                : StompCommand.ERROR;
        var accessor = StompHeaderAccessor.create(command);
        accessor.setLeaveMutable(true);
        var receiptId = clientHeaderAccessor != null ? clientHeaderAccessor.getReceipt() : null;
        if (receiptId != null) {
            accessor.setReceiptId(receiptId);
        }
        accessor.setContentType(MediaType.APPLICATION_JSON);

        return MessageBuilder.createMessage(payload, accessor.getMessageHeaders());
    }

    private boolean shouldSendAsMessage(Throwable e) {
        return e instanceof AppApplicationException;
    }

    private byte[] getPayload(Throwable e, @Nullable StompHeaderAccessor clientHeaderAccessor) {
        var payload = exceptionMapper.getExceptionResponse(e);
        try {
            return objectMapper.writeValueAsBytes(StompResponse.error(payload, clientHeaderAccessor));
        } catch (JsonProcessingException processingException) {
            return EMPTY_PAYLOAD;
        }
    }
}
