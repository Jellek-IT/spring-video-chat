package pl.bronikowski.springchat.backendmain.config.repository;

import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pl.bronikowski.springchat.backendmain.BackendmainApplication;
import pl.bronikowski.springchat.backendmain.config.repository.JpaSpecificationWithEntityGraphImpl;

import java.time.Clock;
import java.util.Map;
import java.util.Optional;

@Configuration
@EnableJpaRepositories(
        basePackageClasses = BackendmainApplication.class,
        repositoryBaseClass = JpaSpecificationWithEntityGraphImpl.class)
@EnableJpaAuditing(dateTimeProviderRef = "dateTimeProvider")
public class RepositoryConfig  implements HibernatePropertiesCustomizer {
    @Bean
    public DateTimeProvider dateTimeProvider(Clock clock) {
        return () -> Optional.of(clock.instant());
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put("hibernate.dialect", "pl.bronikowski.springchat.backendmain.config.repository.CustomPostgreSQLDialect");
    }
}
