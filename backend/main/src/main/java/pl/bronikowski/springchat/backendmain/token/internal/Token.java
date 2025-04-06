package pl.bronikowski.springchat.backendmain.token.internal;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.util.UUID;

@Data
@AllArgsConstructor
@RedisHash("Token")
public class Token {
    @Id
    private final String token;

    @Indexed
    private final TokenType type;

    @Indexed
    private final UUID ownerId;

    @TimeToLive
    private Long expire;
}
