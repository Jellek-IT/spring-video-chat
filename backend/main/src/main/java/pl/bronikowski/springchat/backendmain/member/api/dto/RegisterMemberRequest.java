package pl.bronikowski.springchat.backendmain.member.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import pl.bronikowski.springchat.backendmain.member.api.validation.UniqueNickname;
import pl.bronikowski.springchat.backendmain.member.api.validation.ValidNickname;
import pl.bronikowski.springchat.backendmain.user.api.validation.UniqueEmail;
import pl.bronikowski.springchat.backendmain.user.api.validation.ValidPassword;

public record RegisterMemberRequest(
        @NotEmpty
        @Email
        @UniqueEmail
        String email,
        @ValidNickname
        @UniqueNickname
        String nickname,
        @NotEmpty
        @ValidPassword
        String password
) {
    @Override
    public String nickname() {
        return nickname.strip();
    }
}
