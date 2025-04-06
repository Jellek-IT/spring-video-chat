package pl.bronikowski.springchat.backendnotifications.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Configuration
@RequiredArgsConstructor
public class ThymeleafConfig {
    @Bean
    public TemplateEngine templateEngine(MessageSource messageSource) {
        var templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(htmlTemplateResolver());
        templateEngine.addTemplateResolver(txtTemplateResolver());
        templateEngine.setTemplateEngineMessageSource(messageSource);
        return templateEngine;
    }

    private ITemplateResolver htmlTemplateResolver() {
        var templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("template/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setOrder(1);
        return templateResolver;
    }

    private ITemplateResolver txtTemplateResolver() {
        var templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("template/");
        templateResolver.setSuffix(".txt");
        templateResolver.setTemplateMode(TemplateMode.TEXT);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setOrder(2);
        return templateResolver;
    }
}
