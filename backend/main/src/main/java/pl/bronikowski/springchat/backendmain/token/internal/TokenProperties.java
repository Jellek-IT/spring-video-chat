package pl.bronikowski.springchat.backendmain.token.internal;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.token")
public record TokenProperties(Long userRegisteredConfirmationTtl) {
    public Long getTtl(TokenType type) {
        return switch (type) {
            case USER_REGISTERED_CONFIRMATION -> userRegisteredConfirmationTtl;
        };
    }
}
