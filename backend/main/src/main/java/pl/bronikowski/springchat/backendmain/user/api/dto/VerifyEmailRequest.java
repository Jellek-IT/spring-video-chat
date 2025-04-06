package pl.bronikowski.springchat.backendmain.user.api.dto;

import jakarta.validation.constraints.NotEmpty;

public record VerifyEmailRequest(
        @NotEmpty
        String token
) {
}
