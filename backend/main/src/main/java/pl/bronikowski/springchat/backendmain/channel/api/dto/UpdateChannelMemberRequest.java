package pl.bronikowski.springchat.backendmain.channel.api.dto;

import jakarta.validation.constraints.NotNull;
import pl.bronikowski.springchat.backendmain.channel.api.ChannelMemberRight;

import java.util.Set;
import java.util.UUID;

public record UpdateChannelMemberRequest(
        @NotNull
        UUID memberId,
        @NotNull
        Set<ChannelMemberRight> rights
) {
}
