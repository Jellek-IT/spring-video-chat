package pl.bronikowski.springchat.backendnotifications.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import pl.bronikowski.springchat.backendnotifications.shared.constants.ProjectConstants;

@Configuration
public class MessageSourceConfig {
    private static final String MESSAGE_SOURCE_BASENAME = "classpath:i18n/messages";

    @Bean
    public MessageSource messageSource() {
        var messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename(MESSAGE_SOURCE_BASENAME);
        messageSource.setDefaultEncoding(ProjectConstants.DEFAULT_ENCODING);
        return messageSource;
    }
}
