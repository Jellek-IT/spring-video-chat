package pl.bronikowski.springchat.backendmain.token.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bronikowski.springchat.backendmain.shared.utils.SecureUtils;
import pl.bronikowski.springchat.backendmain.token.api.InvalidTokenException;
import pl.bronikowski.springchat.backendmain.user.internal.User;

@Service
@RequiredArgsConstructor
public class TokenService {
    private static final int DEFAULT_TOKEN_LENGTH = 32;
    private final TokenRepository tokenRepository;
    private final TokenProperties tokenProperties;
    private final SecureUtils secureUtils;

    public String createUserRegistrationConfirmationToken(User user) {
        return createToken(user, TokenType.USER_REGISTERED_CONFIRMATION);
    }

    public void checkAndInvalidateUserRegistrationConfirmationToken(User user, String token) {
        checkAndInvalidateToken(user, token, TokenType.USER_REGISTERED_CONFIRMATION);
    }

    private void checkAndInvalidateToken(User user, String tokenValue, TokenType type) {
        var token = tokenRepository
                .findByTokenAndTokenTypeAndOwnerId(tokenValue, type, user.getId())
                .orElseThrow(InvalidTokenException::new);
        tokenRepository.delete(token);
    }

    private String createToken(User user, TokenType type) {
        var tokenValue = secureUtils.generateToken(DEFAULT_TOKEN_LENGTH);
        var token = new Token(tokenValue, type, user.getId(), tokenProperties.getTtl(type));
        tokenRepository.save(token);
        return tokenValue;
    }
}
