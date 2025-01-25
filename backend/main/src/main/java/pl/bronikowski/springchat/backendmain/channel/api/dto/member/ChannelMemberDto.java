package pl.bronikowski.springchat.backendmain.channel.api.dto.member;

import pl.bronikowski.springchat.backendmain.channel.api.ChannelMemberRight;
import pl.bronikowski.springchat.backendmain.member.api.dto.MemberBasicsDto;

import java.util.Set;

public record ChannelMemberDto(
        MemberBasicsDto member,
        Boolean deleted,
        Set<ChannelMemberRight> rights
) {
}
