package pl.bronikowski.springchat.backendmain.channel.api.dto.message;

import pl.bronikowski.springchat.backendmain.channel.api.validation.ValidChannelMessageText;

public record CreateChannelMessagePayload(
        @ValidChannelMessageText
        String text
) {
}
