package pl.bronikowski.springchat.backendmain.member.api.dto;

import java.util.UUID;

public record MemberBasicsDto(
        UUID id,
        String nickname,
        Boolean hasProfilePicture
) {
}
