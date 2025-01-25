package pl.bronikowski.springchat.backendmain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.bronikowski.springchat.backendmain.shared.constants.DateTimeConstants;

import java.time.Clock;

@Configuration
public class ClockConfig {
    @Bean
    public Clock clock() {
        return Clock.system(DateTimeConstants.DEFAULT_ZONE_OFFSET);
    }
}
