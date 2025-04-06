package pl.bronikowski.springchat.backendnotifications.notification.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import pl.bronikowski.springchat.backendnotifications.shared.enums.LanguageCode;
import pl.bronikowski.springchat.backendnotifications.shared.properties.PlatformProperties;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class NotificationContentEvaluator {
    private static final String NOTIFICATION_MAIL_TEMPLATE_LOCATION = "notification/mail.html";
    private static final String NOTIFICATION_CONTENT_TEMPLATE_LOCATION = "notification/content/%s_%s.txt";
    private static final String NOTIFICATION_TITLE_MESSAGE_CODE = "notification.%s.title";

    private static final String CONTENT_VARIABLE_KEY = "content";
    private static final String PLATFORM_PROPERTIES_VARIABLE_KEY = "platformProperties";
    private static final String TITLE_VARIABLE_KEY = "title";

    private final TemplateEngine templateEngine;
    private final MessageSource messageSource;
    private final PlatformProperties platformProperties;

    public String evaluateNotificationContent(Notification notification) {
        var content = notification.getContent();
        var locale = getLocale();
        var context = new Context(locale);
        context.setVariable(CONTENT_VARIABLE_KEY, content);
        context.setVariable(PLATFORM_PROPERTIES_VARIABLE_KEY, platformProperties);

        var template = String.format(NOTIFICATION_CONTENT_TEMPLATE_LOCATION, content.getType().name(),
                LanguageCode.EN.name());
        return templateEngine.process(template, context);
    }

    public String evaluateNotificationEmail(Notification notification) {
        var locale = getLocale();
        var content = evaluateNotificationContent(notification);
        var context = new Context(locale);
        context.setVariable(CONTENT_VARIABLE_KEY, content);
        context.setVariable(PLATFORM_PROPERTIES_VARIABLE_KEY, platformProperties);
        context.setVariable(TITLE_VARIABLE_KEY, evaluateTitle(notification));

        return templateEngine.process(NOTIFICATION_MAIL_TEMPLATE_LOCATION, context);
    }

    public String evaluateTitle(Notification notification) {
        var content = notification.getContent();
        var locale = getLocale();
        var titleMessageCode = String.format(NOTIFICATION_TITLE_MESSAGE_CODE, content.getType());
        return messageSource.getMessage(titleMessageCode, null, locale);
    }

    private Locale getLocale() {
        return new Locale(LanguageCode.EN.name());
    }
}
