package by.baby.paymentmicroservice.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "test")
public class KafkaTopicConfiguration {

    @Bean
    NewTopic paymentCreatedEventsTopic() {
        return TopicBuilder
                .name("payment-created-events-topic")
                .partitions(3)
                .replicas(3)
                .config("min.insync.replicas", "2")
                .build();
    }
}
