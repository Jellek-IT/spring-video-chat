package pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.bronikowski.springchat.backendmain.config.objectmapper.module.annotation.JsonSubTypeWithTypePropertyAndExistingProperties;
import pl.bronikowski.springchat.backendmain.config.objectmapper.module.annotation.JsonTypeWithTypePropertyAndExistingProperties;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.JanusResponseType;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonTypeWithTypePropertyAndExistingProperties(typeProperty = "janus")
@EqualsAndHashCode
@JsonSubTypeWithTypePropertyAndExistingProperties(typeValue = JanusResponseType.Constants.SUCCESS,
        existingProperties = {"data"},
        value = IdSuccessJanusResponsePayload.class)
@JsonSubTypeWithTypePropertyAndExistingProperties(typeValue = JanusResponseType.Constants.SUCCESS,
        existingProperties = {"plugindata"},
        value = PluginDataSuccessJanusResponsePayload.class)
@JsonSubTypeWithTypePropertyAndExistingProperties(typeValue = JanusResponseType.Constants.ERROR,
        value = ErrorJanusResponsePayload.class)
@JsonSubTypeWithTypePropertyAndExistingProperties(typeValue = JanusResponseType.Constants.ACK,
        value = AckJanusRequestPayload.class)
public class JanusResponsePayload {
    @NotNull
    private final JanusResponseType janus;
    @NotNull
    private final String transaction;
}
