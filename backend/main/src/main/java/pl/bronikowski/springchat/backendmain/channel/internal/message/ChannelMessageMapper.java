package pl.bronikowski.springchat.backendmain.channel.internal.message;

import org.mapstruct.Mapper;
import pl.bronikowski.springchat.backendmain.channel.api.dto.message.ChannelMessageBasicsDto;
import pl.bronikowski.springchat.backendmain.channel.internal.file.ChannelFileMapper;
import pl.bronikowski.springchat.backendmain.config.MapstructConfig;
import pl.bronikowski.springchat.backendmain.member.internal.MemberMapper;

@Mapper(config = MapstructConfig.class, uses = {MemberMapper.class, ChannelFileMapper.class})
public interface ChannelMessageMapper {
    ChannelMessageBasicsDto mapToMessageBasicsDto(ChannelMessage channelMessage);
}
