package pl.bronikowski.springchat.backendmain.videoroom.internal.janus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import pl.bronikowski.springchat.backendmain.config.Profiles;
import pl.bronikowski.springchat.backendmain.videoroom.api.exception.VideoRoomClientException;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.JanusPlugin;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.AckJanusRequestPayload;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.AttachJanusRequestPayload;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.CreateJanusRequestPayload;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.ErrorJanusResponsePayload;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.IdSuccessJanusResponsePayload;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.JanusRequestPayload;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.JanusResponsePayload;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.KeepAliveRequestJacksonPayload;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.MessageJanusRequestPayload;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.messagerequestbody.videoroom.VideoRoomMessageJanusRequestBodyPayload;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
@Slf4j
@RequiredArgsConstructor
public class JanusWebSocketClient implements SmartLifecycle {
    private static final String REALM = "janus";
    private static final String TOKEN_AUTH_ALGORITHM = "HmacSHA1";
    private static final Duration REFRESH_CLIENT_AUTH_TOKEN_THRESHOLD = Duration.ofHours(1);
    private static final String JANUS_PROTOCOL = "janus-protocol";
    private static final Duration KEEP_ALIVE_MESSAGE_RATE = Duration.ofSeconds(30);
    private static final Duration RECONNECT_DELAY = Duration.ofSeconds(10);
    private static final Duration SEND_RESPONSE_TIMEOUT_DELAY = Duration.ofSeconds(10);
    private final WebSocketClient client = new StandardWebSocketClient();
    private final AtomicReference<JanusWebSocketClientStatus> status = new AtomicReference<>(JanusWebSocketClientStatus.DISCONNECTED_RECONNECT);
    private final Object appAuthTokenMonitor = new Object();
    private final Object lifecycleMonitor = new Object();
    private final JanusProperties janusProperties;
    private final TaskScheduler taskScheduler;
    private final ObjectMapper objectMapper;
    private final JanusWebSocketHandler janusWebSocketHandler;
    private final Clock clock;
    private final ReadWriteLock sessionLock = new ReentrantReadWriteLock();
    private WebSocketSession session;
    private VideoRoomAuthTokenDto appAuthToken;
    private volatile Long janusSessionId;
    private volatile Long videoRoomHandleId;
    private volatile boolean isRunning;

    @Override
    public void start() {
        synchronized (lifecycleMonitor) {
            janusWebSocketHandler.attachConnectionClosedHandlers(List.of(() -> {
                status.set(status.get().getDisconnectedStatus());
                setSession(null);
                this.scheduleReconnect();
            }));
            this.status.set(JanusWebSocketClientStatus.DISCONNECTED_RECONNECT);
            this.connect();
            isRunning = true;
        }

    }

    @Override
    public void stop() {
        synchronized (lifecycleMonitor) {
            disconnect(false);
            isRunning = false;
        }
    }

    @Override
    public boolean isRunning() {
        return this.isRunning;
    }

    private void connect() {
        if (!status.compareAndSet(JanusWebSocketClientStatus.DISCONNECTED_RECONNECT, JanusWebSocketClientStatus.CONNECTING)) {
            log.warn("Tried to connect to not disconnected client");
            return;
        }
        log.debug("Connecting");
        var uri = URI.create(janusProperties.wsHost());
        var headers = new WebSocketHttpHeaders();
        headers.setSecWebSocketProtocol(JANUS_PROTOCOL);
        client.execute(janusWebSocketHandler, headers, uri).whenComplete(this::handleConnectionCompleted);
        taskScheduler.scheduleWithFixedDelay(this::sendKeepAlive, KEEP_ALIVE_MESSAGE_RATE);
    }

    private void handleConnectionCompleted(WebSocketSession session, Throwable throwable) {
        if (throwable == null && status.compareAndSet(JanusWebSocketClientStatus.CONNECTING, JanusWebSocketClientStatus.CREATING_SESSION)) {
            setSession(session);
            log.debug("Connected, creating session");
            var transaction = UUID.randomUUID().toString();
            var payload = new CreateJanusRequestPayload(transaction);
            sendWithResponse(payload).whenComplete(this::handleCreatingSessionCompleted);
        } else {
            log.error("Could not connect", throwable);
            status.set(JanusWebSocketClientStatus.DISCONNECTED_RECONNECT);
            scheduleReconnect();
        }
    }

    private void handleCreatingSessionCompleted(JanusResponsePayload payload, Throwable throwable) {
        if (throwable == null
                && (payload instanceof IdSuccessJanusResponsePayload successPayload)
                && status.compareAndSet(JanusWebSocketClientStatus.CREATING_SESSION, JanusWebSocketClientStatus.CREATING_HANDLE)) {
            var sessionId = successPayload.getData().id();
            var transaction = UUID.randomUUID().toString();
            this.janusSessionId = sessionId;
            log.debug("Session created, creating video room handle");
            var attachPayload = new AttachJanusRequestPayload(transaction, sessionId, JanusPlugin.VIDEO_ROOM);
            sendWithResponse(attachPayload).whenComplete(this::handleCreatingHandleCompleted);
        } else {
            logError("Could not create session", payload, throwable);
            disconnect(true);
        }
    }

    private void handleCreatingHandleCompleted(JanusResponsePayload payload, Throwable throwable) {
        if (throwable == null
                && (payload instanceof IdSuccessJanusResponsePayload successPayload)
                && status.compareAndSet(JanusWebSocketClientStatus.CREATING_HANDLE, JanusWebSocketClientStatus.CONNECTED)) {
            videoRoomHandleId = successPayload.getData().id();
            log.debug("Handle created, can accept messages");
        } else {
            logError("Could not create handle", payload, throwable);
            disconnect(true);
        }
    }

    private void sendKeepAlive() {
        var janusSessionId = this.janusSessionId;
        if (janusSessionId == null) {
            return;
        }
        var transaction = UUID.randomUUID().toString();
        var payload = new KeepAliveRequestJacksonPayload(transaction, janusSessionId);
        sendWithResponse(payload).whenComplete(this::handleKeepAliveCompleted);
    }

    private void logError(String text, JanusResponsePayload payload, Throwable throwable) {
        var reason = payload instanceof ErrorJanusResponsePayload errorPayload
                ? errorPayload.toException()
                : throwable;
        log.error(text, reason);
    }

    private void handleKeepAliveCompleted(JanusResponsePayload payload, Throwable throwable) {
        if (throwable != null || !(payload instanceof AckJanusRequestPayload)) {
            logError("Could not get ack response", payload, throwable);
            disconnect(true);
        }
    }

    private void scheduleReconnect() {
        if (status.get().canReconnect()) {
            log.debug("Scheduling reconnect");
            taskScheduler.schedule(this::connect, clock.instant().plus(RECONNECT_DELAY));
        }
    }

    public CompletableFuture<JanusResponsePayload> sendToVideoRoomWithResponse(VideoRoomMessageJanusRequestBodyPayload body) {
        if (!JanusWebSocketClientStatus.CONNECTED.equals(status.get())) {
            var throwable = new VideoRoomClientException(
                    "Could not send message to video room handle. Client is not connected at the moment.");
            return CompletableFuture.failedFuture(throwable);
        }
        var sessionId = this.janusSessionId;
        var handleId = this.videoRoomHandleId;
        if (sessionId == null || handleId == null) {
            var throwable = new VideoRoomClientException(
                    "Could not send message to video room handle. No active video room handle.");
            return CompletableFuture.failedFuture(throwable);
        }
        var transaction = UUID.randomUUID().toString();
        var payload = new MessageJanusRequestPayload(transaction, sessionId, handleId, body);
        return sendWithResponse(payload);
    }

    public VideoRoomAuthTokenDto getJanusAuthToken(Instant expireAt) {

        var secretKeySpec = new SecretKeySpec(janusProperties.tokenAuthSecret().getBytes(), TOKEN_AUTH_ALGORITHM);
        try {
            var data = List.of(
                    String.valueOf(expireAt.getEpochSecond()),
                    REALM,
                    JanusPlugin.VIDEO_ROOM.getBundleName());
            var dataString = String.join(",", data);
            var mac = Mac.getInstance(TOKEN_AUTH_ALGORITHM);
            mac.init(secretKeySpec);
            var signature = Base64.getEncoder().encodeToString(mac.doFinal(dataString.getBytes()));
            return new VideoRoomAuthTokenDto(dataString + ":" + signature, expireAt);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new VideoRoomClientException("Could not create video room videoRoomAccessToken signature");
        }
    }

    private String getAppAuthTokenValue(boolean reload) {
        var expireBefore = clock.instant().plus(REFRESH_CLIENT_AUTH_TOKEN_THRESHOLD);
        synchronized (appAuthTokenMonitor) {
            if (reload || appAuthToken == null || appAuthToken.expireAt().isBefore(expireBefore)) {
                var expireAt = clock.instant().plus(janusProperties.appAuthTokenLifetime());
                appAuthToken = this.getJanusAuthToken(expireAt);
            }
            return appAuthToken.value();
        }
    }

    private void send(JanusRequestPayload payload, boolean reloadToken) {
        payload.setToken(getAppAuthTokenValue(reloadToken));
        var encodedPayload = encodePayload(payload);
        sessionLock.readLock().lock();
        try {
            if (session == null) {
                throw new VideoRoomClientException("There is no active session");
            }
            session.sendMessage(encodedPayload);
        } catch (IOException e) {
            throw new VideoRoomClientException("Could not send message", e);
        } finally {
            sessionLock.readLock().unlock();
        }
    }

    private CompletableFuture<JanusResponsePayload> sendWithResponse(JanusRequestPayload payload) {
        var completableFuture = new CompletableFuture<JanusResponsePayload>();
        var scheduledTimeout = taskScheduler.schedule(() -> {
            if (completableFuture.completeExceptionally(
                    new VideoRoomClientException("Timeout occurred when waiting for response"))) {
                janusWebSocketHandler.detachMessageHandlers(payload.getTransaction());
            }
        }, clock.instant().plus(SEND_RESPONSE_TIMEOUT_DELAY));
        this.janusWebSocketHandler.attachMessageHandler(payload.getTransaction(), (responsePayload) -> {
            if (responsePayload instanceof ErrorJanusResponsePayload errorJanusPayload
                    && errorJanusPayload.getError().code().equals(403)) {
                logError("Client auth videoRoomAccessToken was expired. Resending with refreshed videoRoomAccessToken.", responsePayload, null);
                send(payload, true);
            } else {
                scheduledTimeout.cancel(false);
                completableFuture.complete(responsePayload);
            }
        });
        send(payload, false);
        return completableFuture;
    }

    private void setSession(WebSocketSession session) {
        sessionLock.writeLock().lock();
        try {
            this.session = session;
            if (session == null) {
                janusSessionId = null;
                videoRoomHandleId = null;
            }
        } finally {
            sessionLock.writeLock().unlock();
        }
    }

    private void disconnect(boolean shouldReconnect) {
        var disconnectingStatus = shouldReconnect
                ? JanusWebSocketClientStatus.DISCONNECTING_RECONNECT
                : JanusWebSocketClientStatus.DISCONNECTING;
        status.set(disconnectingStatus);
        sessionLock.writeLock().lock();
        try {
            if (this.session != null) {
                this.session.close();
                this.session = null;
            } else {
                status.set(disconnectingStatus.getDisconnectedStatus());
            }
        } catch (IOException exception) {
            throw new VideoRoomClientException("Could not close existing connection", exception);
        } finally {
            sessionLock.writeLock().unlock();
        }
    }

    private TextMessage encodePayload(JanusRequestPayload payload) {
        try {
            var encodedPayload = objectMapper.writeValueAsString(payload);
            return new TextMessage(encodedPayload);
        } catch (JsonProcessingException e) {
            throw new VideoRoomClientException("Could not encode janus payload", e);
        }
    }
}
