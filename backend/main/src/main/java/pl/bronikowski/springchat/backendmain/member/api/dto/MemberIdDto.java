package pl.bronikowski.springchat.backendmain.member.api.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MemberIdDto(
        @NotNull
        UUID id
) {
}
