package pl.bronikowski.springchat.backendmain.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import pl.bronikowski.springchat.backendmain.BackendmainApplication;

@Configuration
@ConfigurationPropertiesScan(basePackageClasses = BackendmainApplication.class)
public class PropertiesConfig {
}
