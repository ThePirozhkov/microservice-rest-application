package by.baby.paymentmicroservice.service;

import by.baby.event.CreatedPaymentEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.awaitility.Awaitility.await;

@Slf4j
@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Testcontainers
public class TransferServiceTest {

    private static final String TOPIC = "payment-created-events-topic";

    @Autowired
    private KafkaTemplate<String, CreatedPaymentEvent> kafkaTemplate;

    private static final int BROKER_COUNT = 1;

    private static List<KafkaContainer> kafkaContainers;

    private static final CreatedPaymentEvent createdPaymentEvent =
            new CreatedPaymentEvent(1L, 2L, new BigDecimal(1000L));

    @SneakyThrows
    @BeforeAll
    public static void setUp() {

        kafkaContainers = IntStream.range(0, BROKER_COUNT)
                .mapToObj(_ -> new KafkaContainer(DockerImageName.parse("apache/kafka:latest")))
                .peek(KafkaContainer::start)
                .toList();

        waitForBrokerReady();

        waitForClusterReady();

        try (AdminClient adminClient = AdminClient.create(Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainers.getFirst().getBootstrapServers()
        ))) {
            NewTopic topic = new NewTopic(TOPIC, 3, (short) 1);
            adminClient.createTopics(List.of(topic)).all().get();
        }

        waitForTopicReady();

    }

    @Bean
    public Map<String, Object> kafkaConsumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainers.getFirst().getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "payment-created-events");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return props;
    }

    KafkaConsumer<String, String> consumer = new KafkaConsumer<>(kafkaConsumerConfigs());

    @DynamicPropertySource
    public static void kafkaProperties(DynamicPropertyRegistry registry) {
        List<String> brokers = kafkaContainers.stream()
                .map(KafkaContainer::getBootstrapServers)
                .collect(Collectors.toList());
        registry.add("spring.kafka.bootstrap-servers", () -> String.join(",", brokers));
        log.error("Kafka brokers: {}", brokers);
        log.error("Properties: {}", registry);
    }

    private static void waitForBrokerReady() {
        await().atMost(30, TimeUnit.SECONDS).until(() -> {
            try {
                return kafkaContainers.getFirst().getBootstrapServers() != null;
            } catch (Exception e) {
                return false;
            }
        });

        kafkaContainers.forEach(k -> log.info("Kafka broker: {}", k.getBootstrapServers()));
    }

    private static void waitForClusterReady() {
        try (AdminClient adminClient = AdminClient.create(Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                String.join(",", kafkaContainers.stream()
                        .map(KafkaContainer::getBootstrapServers)
                        .toList())))) {

            await().atMost(30, TimeUnit.SECONDS).until(() -> {
                try {
                    Set<String> brokers = adminClient.describeCluster().nodes().get()
                            .stream().map(Node::idString).collect(Collectors.toSet());
                    log.info("Kafka cluster nodes: {}", brokers);
                    return !brokers.isEmpty();
                } catch (Exception e) {
                    return false;
                }
            });

            log.info("Kafka cluster полностью сформирован.");
        } catch (Exception e) {
            log.error("Ошибка при ожидании кластера Kafka", e);
        }
    }

    private static void waitForTopicReady() {
        await().atMost(15, TimeUnit.SECONDS).until(() -> {
            try (AdminClient adminClient = AdminClient.create(Map.of(
                    AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainers.getFirst().getBootstrapServers()
            ))) {
                log.info(adminClient.listTopics().names().get().toString());
                return adminClient.listTopics().names().get().contains(TOPIC);
            }
        });
    }

    private void checkTopic() {
        consumer.subscribe(Collections.singletonList(TOPIC));
        await().atMost(5, TimeUnit.SECONDS)
                .until(() -> {
                    var records = consumer.poll(Duration.ofMillis(1000L));
                    records.forEach(record ->
                            System.out.printf("Consumed record with key: %s, value: %s%n", record.key(), record.value()));
                    return !records.isEmpty();
                });
    }

    @SneakyThrows
    @Test
    public void testKafkaCluster() {

        kafkaTemplate.send(TOPIC, "testKey", createdPaymentEvent);

        try (AdminClient adminClient = AdminClient.create(Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainers.getFirst().getBootstrapServers()))) {

            DescribeTopicsResult topicsResult = adminClient.describeTopics(Collections.singletonList(TOPIC));
            TopicDescription topicDescription = topicsResult.allTopicNames().get().get(TOPIC);

            Map<TopicPartition, OffsetSpec> request = new HashMap<>();
            for (TopicPartitionInfo partitionInfo : topicDescription.partitions()) {
                request.put(new TopicPartition(TOPIC, partitionInfo.partition()), OffsetSpec.latest());
            }

            checkTopic();

            ListOffsetsResult offsetsResult = adminClient.listOffsets(request);
            offsetsResult.all().get().forEach((tp, result) ->
                    log.info("Партиция " + tp.partition() + " содержит " + result.offset() + " сообщений"));
        }
    }


}
