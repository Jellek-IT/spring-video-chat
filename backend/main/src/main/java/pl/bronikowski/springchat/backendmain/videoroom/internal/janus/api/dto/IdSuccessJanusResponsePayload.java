package pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.JanusResponseType;

@Getter
@EqualsAndHashCode(callSuper = true)
public class IdSuccessJanusResponsePayload extends JanusResponsePayload {
    @NotNull
    @Valid
    private final IdSuccessJanusResponsePayload.IdSuccessJanusResponseDataPayload data;

    public IdSuccessJanusResponsePayload(String transaction,
                                         IdSuccessJanusResponseDataPayload data) {
        super(JanusResponseType.SUCCESS, transaction);
        this.data = data;
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record IdSuccessJanusResponseDataPayload(
            @NotNull
            Long id
    ) {
    }
}
