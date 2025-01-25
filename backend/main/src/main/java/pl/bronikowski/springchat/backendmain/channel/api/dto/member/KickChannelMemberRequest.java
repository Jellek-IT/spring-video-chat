package pl.bronikowski.springchat.backendmain.channel.api.dto.member;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record KickChannelMemberRequest(
        @NotNull
        UUID memberId
) {
}
