package pl.bronikowski.springchat.backendmain.config;

import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.Clock;

@Configuration
public class ValidatorConfig {
    @Bean
    public LocalValidatorFactoryBean localValidatorFactoryBean(Clock clock) {
        return new LocalValidatorFactoryBean() {
            @Override
            protected void postProcessConfiguration(@NonNull jakarta.validation.Configuration<?> configuration) {
                configuration.clockProvider(() -> clock);
            }
        };
    }
}
