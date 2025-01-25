package pl.bronikowski.springchat.backendmain.authserver.api;

import java.util.List;

public record UserContext(
        String authResourceId,
        List<String> roles
) {
}
