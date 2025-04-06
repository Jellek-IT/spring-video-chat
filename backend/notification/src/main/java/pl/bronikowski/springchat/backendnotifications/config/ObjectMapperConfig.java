package pl.bronikowski.springchat.backendnotifications.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.bronikowski.springchat.backendnotifications.shared.constants.ProjectConstants;

import java.util.TimeZone;

@Configuration
public class ObjectMapperConfig {
    @Getter
    private static final ObjectMapper instance = new ObjectMapper();

    @Bean
    public ObjectMapper getObjectMapper() {
        return getInstance();
    }

    static {
        instance.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
        instance.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        instance.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        instance.registerModule(new JavaTimeModule());
        instance.setTimeZone(TimeZone.getTimeZone(ProjectConstants.DEFAULT_ZONE_OFFSET));
    }
}
