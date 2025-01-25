package pl.bronikowski.springchat.backendmain.config.objectmapper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.bronikowski.springchat.backendmain.config.objectmapper.module.TypePropertyAndExistingPropertiesModule;
import pl.bronikowski.springchat.backendmain.shared.constants.DateTimeConstants;

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
        instance.registerModule(new ParameterNamesModule());
        instance.registerModule(new TypePropertyAndExistingPropertiesModule());
        instance.setTimeZone(TimeZone.getTimeZone(DateTimeConstants.DEFAULT_ZONE_OFFSET));
    }
}
