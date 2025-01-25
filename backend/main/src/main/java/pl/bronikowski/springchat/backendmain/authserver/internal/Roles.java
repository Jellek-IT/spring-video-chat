package pl.bronikowski.springchat.backendmain.authserver.internal;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Roles {
    public static final String MEMBER = "SPRINGCHAT_MEMBER";

    public static final List<String> ALL_USER_ROLES = List.of(MEMBER);
    public static final String GRANTED_AUTHORITY_PREFIX = "ROLE_";
}
