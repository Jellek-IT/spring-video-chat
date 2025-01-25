package pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.successresponseplugindata.videoroom;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.bronikowski.springchat.backendmain.config.objectmapper.module.annotation.JsonSubTypeWithTypePropertyAndExistingProperties;
import pl.bronikowski.springchat.backendmain.config.objectmapper.module.annotation.JsonTypeWithTypePropertyAndExistingProperties;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.JanusVideoRoomResponseType;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonTypeWithTypePropertyAndExistingProperties(typeProperty = "videoroom")
@JsonSubTypeWithTypePropertyAndExistingProperties(typeValue = JanusVideoRoomResponseType.Constants.EVENT,
        existingProperties = {"error_code", "error"},
        value = ErrorEventVideoRoomSuccessJanusResponsePluginDataDataPayload.class)
@JsonSubTypeWithTypePropertyAndExistingProperties(typeValue = JanusVideoRoomResponseType.Constants.EVENT,
        value = DefaultEventVideoRoomSuccessJanusResponsePluginDataDataPayload.class)
@JsonSubTypeWithTypePropertyAndExistingProperties(typeValue = JanusVideoRoomResponseType.Constants.CREATED,
        value = CreatedVideoRoomSuccessJanusResponsePluginDataDataPayload.class)
@JsonSubTypeWithTypePropertyAndExistingProperties(typeValue = JanusVideoRoomResponseType.Constants.DESTROYED,
        value = DestroyedVideoRoomSuccessJanusResponsePluginDataDataPayload.class)
@JsonSubTypeWithTypePropertyAndExistingProperties(typeValue = JanusVideoRoomResponseType.Constants.SUCCESS,
        existingProperties = {"room", "allowed"},
        value = AllowedSuccessVideoRoomSuccessJanusResponsePluginDataDataPayload.class)
@JsonSubTypeWithTypePropertyAndExistingProperties(typeValue = JanusVideoRoomResponseType.Constants.SUCCESS,
        value = DefaultSuccessVideoRoomSuccessJanusResponsePluginDataDataPayload.class)
public class VideoRoomSuccessJanusResponsePluginDataDataPayload {
    @NotNull
    private final JanusVideoRoomResponseType videoroom;
}
