package pl.bronikowski.springchat.backendmain.videoroom.internal.janus;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.JanusResponsePayload;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
public class JanusWebSocketHandler extends TextWebSocketHandler {
    private final Map<String, List<JanusTextMessageHandler>> textMessageHandlers = new ConcurrentHashMap<>();
    private final List<JanusConnectionClosedHandler> connectionClosedHandlers = new CopyOnWriteArrayList<>();
    private final ObjectMapper objectMapper;
    private final Validator validator;

    public void attachMessageHandler(String transaction, JanusTextMessageHandler handler) {
        textMessageHandlers.computeIfAbsent(transaction, ignored -> new CopyOnWriteArrayList<>())
                .add(handler);
    }

    public void detachMessageHandlers(String transaction) {
        textMessageHandlers.remove(transaction);
    }

    public void attachConnectionClosedHandlers(List<JanusConnectionClosedHandler> handlers) {
        connectionClosedHandlers.addAll(handlers);
    }

    @Override
    protected void handleTextMessage(WebSocketSession webSocketSession, TextMessage message) throws IOException {
        var payload = objectMapper.readValue(message.getPayload(), JanusResponsePayload.class);
        var violations = validator.validate(payload);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Constraint violation", violations);
        }
        var handlers = textMessageHandlers.get(payload.getTransaction());
        if (handlers == null) {
            return;
        }
        handlers.forEach(handler -> handler.run(payload));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus status) throws Exception {
        super.afterConnectionClosed(webSocketSession, status);
        connectionClosedHandlers.forEach(JanusConnectionClosedHandler::run);
    }
}
