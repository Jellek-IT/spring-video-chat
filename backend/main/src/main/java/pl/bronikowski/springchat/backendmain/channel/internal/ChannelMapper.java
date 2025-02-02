package pl.bronikowski.springchat.backendmain.channel.internal;

import org.mapstruct.Mapper;
import pl.bronikowski.springchat.backendmain.channel.api.dto.ChannelBasicsDto;
import pl.bronikowski.springchat.backendmain.channel.api.dto.ChannelDetailsDto;
import pl.bronikowski.springchat.backendmain.channel.internal.member.ChannelMemberMapper;
import pl.bronikowski.springchat.backendmain.config.MapstructConfig;

@Mapper(config = MapstructConfig.class, uses = {ChannelMemberMapper.class})
public interface ChannelMapper {
    ChannelBasicsDto mapToChannelBasicsDto(Channel channel);

    ChannelDetailsDto mapToChannelDetailsDto(Channel channel);
}
