package pl.bronikowski.springchat.backendmain.videoroom.internal.janus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import pl.bronikowski.springchat.backendmain.channel.internal.Channel;
import pl.bronikowski.springchat.backendmain.exception.AppNotFoundException;
import pl.bronikowski.springchat.backendmain.shared.properties.AppProperties;
import pl.bronikowski.springchat.backendmain.shared.utils.SecureUtils;
import pl.bronikowski.springchat.backendmain.user.internal.User;
import pl.bronikowski.springchat.backendmain.videoroom.api.dto.VideoRoomSessionDetailsDto;
import pl.bronikowski.springchat.backendmain.videoroom.api.exception.VideoRoomAlreadyJoinedException;
import pl.bronikowski.springchat.backendmain.videoroom.api.exception.VideoRoomClientException;
import pl.bronikowski.springchat.backendmain.videoroom.internal.VideoRoomClient;
import pl.bronikowski.springchat.backendmain.videoroom.internal.activevideoroom.ActiveVideoRoom;
import pl.bronikowski.springchat.backendmain.videoroom.internal.activevideoroom.ActiveVideoRoomRepository;
import pl.bronikowski.springchat.backendmain.videoroom.internal.activevideoroomparticipant.ActiveVideoRoomParticipant;
import pl.bronikowski.springchat.backendmain.videoroom.internal.activevideoroomparticipant.ActiveVideoRoomParticipantRepository;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.JanusResponsePayload;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.PluginDataSuccessJanusResponsePayload;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.messagerequestbody.videoroom.AllowedVideoRoomMessageJanusRequestBodyPayload;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.messagerequestbody.videoroom.CreateVideoRoomMessageJanusRequestBodyPayload;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.messagerequestbody.videoroom.DestroyVideoRoomMessageJanusRequestBodyPayload;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.successresponseplugindata.videoroom.AllowedSuccessVideoRoomSuccessJanusResponsePluginDataDataPayload;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.successresponseplugindata.videoroom.CreatedVideoRoomSuccessJanusResponsePluginDataDataPayload;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.successresponseplugindata.videoroom.DestroyedVideoRoomSuccessJanusResponsePluginDataDataPayload;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.successresponseplugindata.videoroom.ErrorEventVideoRoomSuccessJanusResponsePluginDataDataPayload;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.successresponseplugindata.videoroom.VideoRoomSuccessJanusResponsePluginDataDataPayload;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.videoroom.VideoRoomAllowedAction;
import pl.bronikowski.springchat.backendmain.websocket.api.UserConnectionDetails;
import pl.bronikowski.springchat.backendmain.websocket.api.UserSubscriptionDetails;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class JanusVideoRoomClient implements VideoRoomClient {
    private static final Duration EXPIRATION_THRESHOLD = Duration.ofMinutes(10);
    private static final String SESSION_SEPARATOR = ";";
    private static final String VIDEO_ROOM_USER_TOKEN_SEPARATOR = ";";
    private static final int DUMMY_ALLOWED_TOKEN_LENGTH = 64;
    private static final int ROOM_SECRET_LENGTH = 64;
    private static final int ALLOWED_TOKEN_LENGTH = 32;
    private final JanusWebSocketClient janusWebSocketClient;
    private final JanusProperties janusProperties;
    private final ActiveVideoRoomRepository activeVideoRoomRepository;
    private final ActiveVideoRoomParticipantRepository activeVideoRoomParticipantRepository;
    private final AppProperties appProperties;
    private final Clock clock;
    private final SecureUtils secureUtils;

    @Override
    public VideoRoomSessionDetailsDto join(Channel channel, User user, UserSubscriptionDetails userSubscriptionDetails) {
        if (activeVideoRoomParticipantRepository.existsByAuthResourceIdAndExpireAtAfter(
                userSubscriptionDetails.authResourceId(), clock.instant())) {
            throw new VideoRoomAlreadyJoinedException();
        }
        var room = activeVideoRoomRepository.findByChannelIdAndExpireAtAfter(channel.getId(), clock.instant())
                .orElseGet(() -> create(channel));
        var participant = addParticipant(room, user, userSubscriptionDetails);
        return mapToSessionDetails(room, participant);
    }

    @Override
    public void leaveWithSubscriptionDetails(UserSubscriptionDetails userSubscriptionDetails) {
        var authResourceId = userSubscriptionDetails.authResourceId();
        var sessionId = getSessionId(userSubscriptionDetails.stompSessionId());
        var subscriptionId = userSubscriptionDetails.stompSubscriptionId();
        activeVideoRoomParticipantRepository.findByAuthResourceIdAndSessionIdAndSubscriptionIdAndExpireAtAfter(
                        authResourceId, sessionId, subscriptionId, clock.instant())
                .forEach(this::expireParticipant);
    }

    @Override
    public void leave(UserConnectionDetails userConnectionDetails) {
        var authResourceId = userConnectionDetails.authResourceId();
        var sessionId = getSessionId(userConnectionDetails.stompSessionId());
        activeVideoRoomParticipantRepository.findByAuthResourceIdAndSessionIdAndExpireAtAfter(authResourceId, sessionId,
                        clock.instant())
                .ifPresent(this::expireParticipant);
    }

    @Override
    public VideoRoomSessionDetailsDto extendUserSession(UserConnectionDetails userConnectionDetails) {
        var authResourceId = userConnectionDetails.authResourceId();
        var sessionId = getSessionId(userConnectionDetails.stompSessionId());
        var participant = activeVideoRoomParticipantRepository.findByAuthResourceIdAndSessionIdAndExpireAtAfter(
                authResourceId, sessionId, clock.instant()).orElseThrow(AppNotFoundException::new);
        var room = activeVideoRoomRepository.findById(participant.getRoomId()).orElseThrow(AppNotFoundException::new);

        participant.setExpireAt(getActiveParticipantExpireAt());
        activeVideoRoomParticipantRepository.saveWithExpireAtUpdate(participant);
        room.setExpireAt(getActiveVideoRoomExpireAt());
        activeVideoRoomRepository.saveWithExpireAtUpdate(room);

        return mapToSessionDetails(room, participant);
    }

    private VideoRoomSessionDetailsDto mapToSessionDetails(ActiveVideoRoom room, ActiveVideoRoomParticipant participant) {

        var token = janusWebSocketClient.getJanusAuthToken(participant.getExpireAt());
        return new VideoRoomSessionDetailsDto(room.getChannelId(), room.getVideoRoomId(), token,
                participant.getVideoRoomAccessToken());
    }

    /**
     * First expired rooms are removed and all participants entries in it. Next expired participants in the rest of
     * rooms are removed
     */
    @Override
    public void clearExpired() {
        var destroyFutures = StreamSupport.stream(activeVideoRoomRepository.findExpired(
                        clock.instant().minus(EXPIRATION_THRESHOLD)).spliterator(), false)
                .map(this::getDestroyFuture)
                .toList();
        var destroyFuturesArray = destroyFutures.stream()
                .map(Pair::getSecond)
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(destroyFuturesArray).join();
        destroyFutures.forEach(destroyFuture ->
                handleDestroyResponse(destroyFuture.getFirst(), destroyFuture.getSecond()));

        var removeParticipantFutures = StreamSupport.stream(activeVideoRoomParticipantRepository.findExpired(
                        clock.instant().minus(EXPIRATION_THRESHOLD)).spliterator(), false)
                .map(this::getRemoveParticipantFuture)
                .toList();
        var removeParticipantFuturesArray = removeParticipantFutures.stream()
                .map(Pair::getSecond)
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(removeParticipantFuturesArray).join();
        removeParticipantFutures
                .forEach(removeParticipantFuture -> handleRemoveParticipantResponse(removeParticipantFuture.getFirst(),
                        removeParticipantFuture.getSecond()));
    }

    private ActiveVideoRoom create(Channel channel) {
        var secret = generateSecret();
        var payload = new CreateVideoRoomMessageJanusRequestBodyPayload();
        payload.setDescription(channel.getId().toString());
        payload.setAdminKey(janusProperties.videoRoomAdminKey());
        payload.setIsPrivate(true);
        payload.setSecret(secret);
        payload.setRequirePvtid(true);
        payload.setSignedTokens(false);
        payload.setPublishers(janusProperties.publishers());
        payload.setLockRecord(true);
        payload.setRequireE2ee(false);
        // one dummy token so that joining to room always require token
        payload.setAllowed(List.of(generateDummyAllowedToken()));

        var response = janusWebSocketClient.sendToVideoRoomWithResponse(payload).join();
        var responseData = getVideoRoomResponseData(response);
        if (!(responseData instanceof CreatedVideoRoomSuccessJanusResponsePluginDataDataPayload createdData)) {
            throw new VideoRoomClientException("Unknown error occurred when creating video room");
        }
        var activeVideoRoom = new ActiveVideoRoom(UUID.randomUUID(), channel.getId(), createdData.getRoom(), secret,
                getActiveVideoRoomExpireAt());
        return activeVideoRoomRepository.saveWithExpireAtUpdate(activeVideoRoom);
    }

    private ActiveVideoRoomParticipant addParticipant(ActiveVideoRoom activeVideoRoom, User user,
                                                      UserSubscriptionDetails userSubscriptionDetails) {
        var payload = new AllowedVideoRoomMessageJanusRequestBodyPayload();
        var token = generateAllowedToken(user);
        var expireAt = getActiveParticipantExpireAt();
        var authResourceId = user.getAuthResourceId();
        var sessionId = getSessionId(userSubscriptionDetails.stompSessionId());
        var subscriptionId = userSubscriptionDetails.stompSubscriptionId();
        var participant = new ActiveVideoRoomParticipant(UUID.randomUUID(), authResourceId, sessionId, subscriptionId,
                token, expireAt, activeVideoRoom.getId());
        payload.setSecret(activeVideoRoom.getVideoRoomSecret());
        payload.setRoom(activeVideoRoom.getVideoRoomId());
        payload.setAction(VideoRoomAllowedAction.ADD);
        payload.setAllowed(List.of(token));
        var response = janusWebSocketClient.sendToVideoRoomWithResponse(payload).join();
        var responseData = getVideoRoomResponseData(response);
        if (!(responseData instanceof AllowedSuccessVideoRoomSuccessJanusResponsePluginDataDataPayload)) {
            throw new VideoRoomClientException("Unknown error occurred when joining video room");
        }
        activeVideoRoomParticipantRepository.saveWithExpireAtUpdate(participant);
        activeVideoRoom.setExpireAt(getActiveVideoRoomExpireAt());
        activeVideoRoomRepository.saveWithExpireAtUpdate(activeVideoRoom);
        return participant;
    }

    private void expireParticipant(ActiveVideoRoomParticipant participant) {
        participant.setExpireAt(clock.instant());
        activeVideoRoomParticipantRepository.saveWithExpireAtUpdate(participant);
    }

    private Pair<ActiveVideoRoomParticipant, CompletableFuture<JanusResponsePayload>> getRemoveParticipantFuture(
            ActiveVideoRoomParticipant participant) {
        var room = activeVideoRoomRepository.findById(participant.getRoomId()).orElseThrow(AppNotFoundException::new);
        var payload = new AllowedVideoRoomMessageJanusRequestBodyPayload();
        payload.setSecret(room.getVideoRoomSecret());
        payload.setRoom(room.getVideoRoomId());
        payload.setAction(VideoRoomAllowedAction.REMOVE);
        payload.setAllowed(List.of(participant.getVideoRoomAccessToken()));
        return Pair.of(participant, janusWebSocketClient.sendToVideoRoomWithResponse(payload));
    }

    private void handleRemoveParticipantResponse(ActiveVideoRoomParticipant participant,
                                                 CompletableFuture<JanusResponsePayload> future) {
        var response = future.join();
        if (!(response instanceof PluginDataSuccessJanusResponsePayload pluginDataSuccessResponse)) {
            log.error("Unknown error occurred, when trying to remove participant [{}]", participant.getId());
            return;
        }
        var responseData = pluginDataSuccessResponse.getPlugindata().data();
        if (responseData instanceof ErrorEventVideoRoomSuccessJanusResponsePluginDataDataPayload errorEventData) {
            log.error("An error occurred, when trying to remove participant [{}], error code: {}", participant.getId(),
                    errorEventData.getErrorCode());
            return;
        }
        if (!(responseData instanceof AllowedSuccessVideoRoomSuccessJanusResponsePluginDataDataPayload)) {
            log.error("Unknown error occurred, when trying to remove participant [{}]", participant.getId());
            return;
        }
        activeVideoRoomParticipantRepository.deleteWithExpireAt(participant);
    }

    private Pair<ActiveVideoRoom, CompletableFuture<JanusResponsePayload>> getDestroyFuture(ActiveVideoRoom room) {
        var payload = new DestroyVideoRoomMessageJanusRequestBodyPayload();
        payload.setRoom(room.getVideoRoomId());
        payload.setSecret(room.getVideoRoomSecret());
        payload.setPermanent(true);
        return Pair.of(room, janusWebSocketClient.sendToVideoRoomWithResponse(payload));
    }

    private void handleDestroyResponse(ActiveVideoRoom room, CompletableFuture<JanusResponsePayload> future) {
        var response = future.join();
        if (!(response instanceof PluginDataSuccessJanusResponsePayload pluginDataSuccessResponse)) {
            log.error("Unknown error occurred, when trying to destroy room [{}]", room.getId());
            return;
        }
        var responseData = pluginDataSuccessResponse.getPlugindata().data();
        if (responseData instanceof ErrorEventVideoRoomSuccessJanusResponsePluginDataDataPayload errorEventData) {
            if (JanusVideoRoomErrorCodes.JANUS_VIDEOROOM_ERROR_NO_SUCH_ROOM == errorEventData.getErrorCode()) {
                clearActiveRoomEntry(room);
            } else {
                log.error("An error occurred, when trying to destroy room [{}], error code: {}", room.getId(),
                        errorEventData.getErrorCode());
            }
            return;
        }
        if (!(responseData instanceof DestroyedVideoRoomSuccessJanusResponsePluginDataDataPayload)) {
            log.error("Unknown error occurred, when trying to remove participant [{}]", room.getId());
            return;
        }
        clearActiveRoomEntry(room);
    }

    private void clearActiveRoomEntry(ActiveVideoRoom room) {
        activeVideoRoomRepository.deleteWithExpireAt(room);
    }

    private String generateSecret() {
        return secureUtils.generateToken(ROOM_SECRET_LENGTH);
    }

    private String generateAllowedToken(User user) {
        var token = secureUtils.generateToken(ALLOWED_TOKEN_LENGTH);
        return user.getId().toString() + VIDEO_ROOM_USER_TOKEN_SEPARATOR + token;
    }

    private String generateDummyAllowedToken() {
        return secureUtils.generateToken(DUMMY_ALLOWED_TOKEN_LENGTH);
    }

    private Instant getActiveParticipantExpireAt() {
        return clock.instant().plus(janusProperties.clientAuthTokenLifetime());
    }

    private Instant getActiveVideoRoomExpireAt() {
        return clock.instant().plus(janusProperties.videoRoomLifeTime());
    }

    private static VideoRoomSuccessJanusResponsePluginDataDataPayload getVideoRoomResponseData(JanusResponsePayload response) {
        if (!(response instanceof PluginDataSuccessJanusResponsePayload pluginDataSuccessResponse)) {
            throw new VideoRoomClientException("Unknown error occurred for janus request");
        }
        var responseData = pluginDataSuccessResponse.getPlugindata().data();
        if (responseData instanceof ErrorEventVideoRoomSuccessJanusResponsePluginDataDataPayload errorEventData) {
            throw new VideoRoomClientException("Error occurred for janus request. error code: " + errorEventData.getErrorCode());
        }
        return responseData;
    }

    private String getSessionId(String sessionId) {
        return appProperties.id() + SESSION_SEPARATOR + sessionId;
    }
}
