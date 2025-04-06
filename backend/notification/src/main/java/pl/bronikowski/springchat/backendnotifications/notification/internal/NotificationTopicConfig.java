package pl.bronikowski.springchat.backendnotifications.notification.internal;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import pl.bronikowski.springchat.backendnotifications.notification.api.dto.NotificationDto;

@Configuration
public class NotificationTopicConfig {
    private static final int POOL_TIMEOUT = 3 * 1000; // 3 seconds

    @Bean
    public NewTopic notificationTopic(NotificationTopicProperties notificationTopicProperties) {
        return TopicBuilder
                .name(notificationTopicProperties.name())
                .partitions(notificationTopicProperties.partitions())
                .replicas(notificationTopicProperties.replicas())
                .build();
    }

    @Bean
    public ConsumerFactory<String, NotificationDto> notificationConsumerFactory(KafkaProperties kafkaProperties) {
        return new DefaultKafkaConsumerFactory<>(
                kafkaProperties.buildConsumerProperties(),
                new StringDeserializer(),
                new JsonDeserializer<>(NotificationDto.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NotificationDto> notificationKafkaListenerContainerFactory(
            ConsumerFactory<String, NotificationDto> notificationDtoConsumerFactory) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, NotificationDto>();
        factory.setConsumerFactory(notificationDtoConsumerFactory);
        factory.setBatchListener(true);
        factory.getContainerProperties().setPollTimeout(POOL_TIMEOUT);
        return factory;
    }


}
