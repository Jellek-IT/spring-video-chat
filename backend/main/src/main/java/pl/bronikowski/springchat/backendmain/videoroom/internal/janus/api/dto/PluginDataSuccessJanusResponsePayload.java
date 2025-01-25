package pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.JanusResponseType;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.successresponseplugindata.SuccessJanusResponsePluginDataPayload;

@Getter
@EqualsAndHashCode(callSuper = true)
public class PluginDataSuccessJanusResponsePayload extends JanusResponsePayload {
    @NotNull
    @Valid
    private final SuccessJanusResponsePluginDataPayload plugindata;

    public PluginDataSuccessJanusResponsePayload(String transaction,
                                                 SuccessJanusResponsePluginDataPayload plugindata) {
        super(JanusResponseType.SUCCESS, transaction);
        this.plugindata = plugindata;
    }
}
