package pl.bronikowski.springchat.backendmain.token.internal;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends CrudRepository<Token, UUID> {
    Optional<Token> findByTokenAndTokenTypeAndOwnerId(String token, TokenType type, UUID ownerId);
}
