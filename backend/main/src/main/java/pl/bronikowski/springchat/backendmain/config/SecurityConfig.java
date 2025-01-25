package pl.bronikowski.springchat.backendmain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;

@Configuration
public class SecurityConfig {
    @Bean
    public SecureRandom getSecureRandom() {
        return new SecureRandom();
    }
}
