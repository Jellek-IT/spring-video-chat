package pl.bronikowski.springchat.backendmain.shared.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class SecureUtils {
    private final SecureRandom secureRandom;

    public String generateToken(int length) {
        var token = new byte[length];
        secureRandom.nextBytes(token);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token);
    }
}
