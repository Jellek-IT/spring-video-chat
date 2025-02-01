package pl.bronikowski.springchat.backendmain.channel.api.dto.file;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ChannelFileIdDto(
        @NotNull
        UUID id
) {
}
