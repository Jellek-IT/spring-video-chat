package pl.bronikowski.springchat.backendnotifications.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import pl.bronikowski.springchat.backendnotifications.BackendnotificationsApplication;

@Configuration
@ConfigurationPropertiesScan(basePackageClasses = BackendnotificationsApplication.class)
public class PropertiesConfig {
}
