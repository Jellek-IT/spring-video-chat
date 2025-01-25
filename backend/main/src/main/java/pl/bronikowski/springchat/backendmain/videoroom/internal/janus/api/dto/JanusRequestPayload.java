package pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.JanusRequestType;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@EqualsAndHashCode
public class JanusRequestPayload {
    @NotNull
    private final JanusRequestType janus;
    @NotNull
    private final String transaction;
    @NotNull
    @Setter
    private String token;
}
