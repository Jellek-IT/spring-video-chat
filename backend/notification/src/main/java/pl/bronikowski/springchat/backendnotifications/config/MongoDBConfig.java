package pl.bronikowski.springchat.backendnotifications.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "pl.bronikowski.springchat.backendnotifications")
public class MongoDBConfig {

}
