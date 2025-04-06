package pl.bronikowski.springchat.backendnotifications;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;

@SpringBootApplication(exclude = ThymeleafAutoConfiguration.class)
public class BackendnotificationsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendnotificationsApplication.class, args);
    }

}
