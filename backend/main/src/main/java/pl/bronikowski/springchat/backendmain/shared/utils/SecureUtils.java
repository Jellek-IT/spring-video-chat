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
        var pin = new byte[length];
        secureRandom.nextBytes(pin);
        return Base64.getEncoder().withoutPadding().encodeToString(pin);
    }
}
