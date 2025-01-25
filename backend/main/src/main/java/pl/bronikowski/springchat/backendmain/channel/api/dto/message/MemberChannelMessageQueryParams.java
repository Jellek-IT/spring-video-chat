package pl.bronikowski.springchat.backendmain.channel.api.dto.message;

public record MemberChannelMessageQueryParams(
        Long beforeSequence
) {
}
