package by.baby.paymentmicroservice;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;


@SpringBootTest
@ActiveProfiles({"kafka", "test"})
@Testcontainers
@Slf4j
public class KafkaTest {

    private static final KafkaContainer kafkaContainer =
            new KafkaContainer(DockerImageName.parse("apache/kafka"));

    protected static AdminClient adminClient;

    @Autowired
    private KafkaConsumer<String, Object> consumer;

    @Configuration
    @Profile("kafka")
    static class TestConfiguration {
        @Bean
        public Map<String, Object> kafkaConsumerConfigs() {
            Map<String, Object> props = new HashMap<>();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
            props.put(ConsumerConfig.GROUP_ID_CONFIG, "payment-events");
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            props.put(JsonDeserializer.TRUSTED_PACKAGES, "by.baby.event");
            return props;
        }

        @Bean
        public KafkaConsumer<String, Object> kafkaConsumer() {
            return new KafkaConsumer<>(kafkaConsumerConfigs());
        }
    }

    @SneakyThrows
    @BeforeAll
    public static void setup1() {
        kafkaContainer.start();

        adminClient = AdminClient.create(Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers()));

        waitForBrokerReady();

    }

    @AfterAll
    public static void shutdown() {
        adminClient.close();
        kafkaContainer.close();
    }

    private static void waitForBrokerReady() {
        await().atMost(30, TimeUnit.SECONDS).until(() -> {
            try {
                return kafkaContainer.getBootstrapServers() != null;
            } catch (Exception e) {
                return false;
            }
        });

        log.info("Kafka broker-{} is ready", kafkaContainer.getBootstrapServers());
    }

    @DynamicPropertySource
    public static void kafkaProperties(@NotNull DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @SneakyThrows
    protected void startTopic(String topicName, int partitions) {
        NewTopic topic = new NewTopic(topicName, partitions, (short) 1);
        adminClient.createTopics(List.of(topic)).all().get();

        await().atMost(15, TimeUnit.SECONDS).until(() -> {
            try (AdminClient adminClient = AdminClient.create(Map.of(
                    AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers()
            ))) {
                log.info(adminClient.listTopics().names().get().toString());
                return adminClient.listTopics().names().get().contains(topicName);
            }
        });
    }

    protected void checkTopic(String topicName) {
        consumer.subscribe(Collections.singletonList(topicName));
        await().atMost(5, TimeUnit.SECONDS)
                .until(() -> {
                    var records = consumer.poll(Duration.ofMillis(1000L));
                    records.forEach(record ->
                            log.info("Consumed record with key: {}, value: {}", record.key(), record.value()));
                    return !records.isEmpty();
                });
    }

}
