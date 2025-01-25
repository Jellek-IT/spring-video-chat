package pl.bronikowski.springchat.backendmain.channel.internal.member;

import org.mapstruct.Mapper;
import pl.bronikowski.springchat.backendmain.channel.api.dto.member.ChannelMemberDto;
import pl.bronikowski.springchat.backendmain.config.MapstructConfig;
import pl.bronikowski.springchat.backendmain.member.internal.MemberMapper;

@Mapper(config = MapstructConfig.class, uses = {MemberMapper.class})
public interface ChannelMemberMapper {
    ChannelMemberDto mapToChannelMemberDto(ChannelMember channelMember);
}
