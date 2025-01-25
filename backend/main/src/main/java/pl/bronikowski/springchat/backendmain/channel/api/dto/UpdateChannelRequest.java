package pl.bronikowski.springchat.backendmain.channel.api.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateChannelRequest(
        @NotBlank
        String name
) {
}
