package pl.bronikowski.springchat.backendnotifications.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.bronikowski.springchat.backendnotifications.shared.constants.ProjectConstants;

import java.time.Clock;

@Configuration
public class ClockConfig {
    @Bean
    public Clock clock() {
        return Clock.system(ProjectConstants.DEFAULT_ZONE_OFFSET);
    }
}
