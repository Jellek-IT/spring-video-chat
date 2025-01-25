package pl.bronikowski.springchat.backendmain.channel.api.dto;

import jakarta.validation.constraints.NotNull;
import pl.bronikowski.springchat.backendmain.channel.api.validation.ValidChannelName;

public record CreateChannelRequest(
        @NotNull
        @ValidChannelName
        String name
) {
    @Override
    public String name() {
        return name.strip();
    }
}
