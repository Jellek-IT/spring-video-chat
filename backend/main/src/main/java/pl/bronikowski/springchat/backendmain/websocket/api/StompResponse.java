package pl.bronikowski.springchat.backendmain.websocket.api;

import jakarta.annotation.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import pl.bronikowski.springchat.backendmain.websocket.internal.StompConstants;

public record StompResponse<T>(
        StompResponseType responseType,
        String transactionId,
        T data
) {
    public static <T> StompResponse<T> message(T data, Message<?> message) {
        var accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        return message(data, accessor);
    }

    public static <T> StompResponse<T> message(T data, @Nullable StompHeaderAccessor stompHeaderAccessor) {
        return new StompResponse<T>(StompResponseType.MESSAGE, getTransactionId(stompHeaderAccessor), data);
    }

    public static <T> StompResponse<T> error(T data, Message<?> message) {
        var accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        return error(data, accessor);
    }

    public static <T> StompResponse<T> error(T data, @Nullable StompHeaderAccessor stompHeaderAccessor) {
        return new StompResponse<T>(StompResponseType.ERROR, getTransactionId(stompHeaderAccessor), data);
    }

    /* for MESSAGE receipt to work there should be alsa defined ApplicationDestinationPrefixes as
     * enableStompBrokerRelay, but SEND command will not have it included, for that reason there
     * is additional custom transaction included for SENT command */
    @Nullable
    private static String getTransactionId(@Nullable StompHeaderAccessor stompHeaderAccessor) {
        if (stompHeaderAccessor == null) {
            return null;
        }
        var transactionId = stompHeaderAccessor.getNativeHeader(StompConstants.APP_TRANSACTION_HEADER);
        if (transactionId == null) {
            return null;
        }
        return transactionId.stream().findFirst().orElse(null);
    }
}
