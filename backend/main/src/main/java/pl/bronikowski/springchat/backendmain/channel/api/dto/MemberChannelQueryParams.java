package pl.bronikowski.springchat.backendmain.channel.api.dto;

public record MemberChannelQueryParams(
        String name,
        Long offset
) {
}
