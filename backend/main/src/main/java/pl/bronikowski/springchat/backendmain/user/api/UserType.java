package pl.bronikowski.springchat.backendmain.user.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.bronikowski.springchat.backendmain.authserver.internal.Roles;

@RequiredArgsConstructor
@Getter
public enum UserType {
    MEMBER(Roles.MEMBER);

    private final String role;
}
