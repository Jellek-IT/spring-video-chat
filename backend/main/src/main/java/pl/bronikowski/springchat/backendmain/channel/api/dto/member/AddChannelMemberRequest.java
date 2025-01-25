package pl.bronikowski.springchat.backendmain.channel.api.dto.member;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import pl.bronikowski.springchat.backendmain.channel.api.ChannelMemberRight;
import pl.bronikowski.springchat.backendmain.member.api.dto.MemberIdDto;

import java.util.Set;

public record AddChannelMemberRequest(
        @NotNull
        @Valid
        MemberIdDto member,
        Set<ChannelMemberRight> rights
) {
}
