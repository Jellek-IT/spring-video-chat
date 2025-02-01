package pl.bronikowski.springchat.backendmain.channel.api.dto.file;

import pl.bronikowski.springchat.backendmain.channel.api.ChannelFileType;

import java.time.Instant;
import java.util.UUID;

public record ChannelFileDto(
        UUID id,
        Instant createdAt,
        ChannelFileType type
) {
}
