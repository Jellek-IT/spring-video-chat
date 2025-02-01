package pl.bronikowski.springchat.backendmain.channel.api.dto.message;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import pl.bronikowski.springchat.backendmain.channel.api.dto.file.ChannelFileIdDto;
import pl.bronikowski.springchat.backendmain.channel.api.validation.ChannelMessageWithContent;
import pl.bronikowski.springchat.backendmain.channel.api.validation.ValidChannelMessageText;

import java.util.List;

@ChannelMessageWithContent
public record CreateChannelMessagePayload(
        @NotNull
        @ValidChannelMessageText
        String text,
        @NotNull
        @Valid
        List<ChannelFileIdDto> files
) {
}
