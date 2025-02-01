package pl.bronikowski.springchat.backendmain.channel.internal.file;

import org.mapstruct.Mapper;
import pl.bronikowski.springchat.backendmain.channel.api.dto.file.ChannelFileDto;
import pl.bronikowski.springchat.backendmain.config.MapstructConfig;

@Mapper(config = MapstructConfig.class)
public interface ChannelFileMapper {
    ChannelFileDto mapToChannelFileDto(ChannelFile channelFile);
}
