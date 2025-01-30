package by.baby.paymentmicroservice.service;

import by.baby.event.CreatedPaymentEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
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

    @SneakyThrows
    @BeforeAll
    public static void setUp() {
        Network network = Network.newNetwork();

          kafkaContainers = IntStream.range(0, BROKER_COUNT)
                .mapToObj(i -> new KafkaContainer(DockerImageName.parse("apache/kafka:latest"))
                        .withNetwork(network)
                        .withNetworkAliases("kafka-" + i))
                .peek(KafkaContainer::start)
                .toList();

        await().atMost(30, TimeUnit.SECONDS).until(() -> {
            try {
                return kafkaContainers.getFirst().getBootstrapServers() != null;
            } catch (Exception e) {
                return false;
            }
        });

        kafkaContainers.forEach(k -> log.info("Kafka broker: {}", k.getBootstrapServers()));


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
                    return !brokers.isEmpty();  // Дождаться всех брокеров
                } catch (Exception e) {
                    return false;
                }
            });

            log.info("Kafka cluster полностью сформирован.");
        } catch (Exception e) {
            log.error("Ошибка при ожидании кластера Kafka", e);
        }

        try (AdminClient adminClient = AdminClient.create(Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainers.getFirst().getBootstrapServers()
        ))) {
            NewTopic topic = new NewTopic(TOPIC, 3, (short) 1);
            adminClient.createTopics(List.of(topic)).all().get();
        }

        waitForKafkaReady();

    }

    @DynamicPropertySource
    public static void kafkaProperties(DynamicPropertyRegistry registry) {
        List<String> brokers = kafkaContainers.stream()
                .map(KafkaContainer::getBootstrapServers)
                .collect(Collectors.toList());
        registry.add("spring.kafka.bootstrap-servers", () -> String.join(",", brokers));
        log.error("Kafka brokers: {}", brokers);
        log.error("Properties: {}", registry);
    }

    private static void waitForKafkaReady() {
        try (AdminClient adminClient = AdminClient.create(Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainers.getFirst().getBootstrapServers()))) {

            await().atMost(30, TimeUnit.SECONDS).until(() -> {
                try {
                    return !adminClient.listTopics().names().get().isEmpty();
                } catch (Exception e) {
                    return false;
                }
            });

            log.info("Kafka готов к работе.");
        } catch (Exception e) {
            log.error("Ошибка при ожидании Kafka", e);
        }
    }

    @SneakyThrows
    @Test
    public void testKafkaCluster() {
        await().atMost(15, TimeUnit.SECONDS).until(() -> {
            try (AdminClient adminClient = AdminClient.create(Map.of(
                    AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainers.getFirst().getBootstrapServers()
            ))) {
                log.info(adminClient.listTopics().names().get().toString());
                return adminClient.listTopics().names().get().contains(TOPIC);
            }
        });

        kafkaTemplate.send(TOPIC, "testKey", new CreatedPaymentEvent(1L, 2L, new BigDecimal(1000L)));

        await().atMost(5, TimeUnit.SECONDS).until(() -> true); // здесь необходимо добавить ожидание обработки сообщения


        try (AdminClient adminClient = AdminClient.create(Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainers.getFirst().getBootstrapServers()))) {
            DescribeTopicsResult topicsResult = adminClient.describeTopics(Collections.singletonList(TOPIC));
            TopicDescription topicDescription = topicsResult.all().get().get(TOPIC);

            Map<TopicPartition, OffsetSpec> request = new HashMap<>();
            for (TopicPartitionInfo partitionInfo : topicDescription.partitions()) {
                request.put(new TopicPartition(TOPIC, partitionInfo.partition()), OffsetSpec.latest());
            }

            ListOffsetsResult offsetsResult = adminClient.listOffsets(request);
            offsetsResult.all().get().forEach((tp, result) ->
                    System.out.println("Партиция " + tp.partition() + " содержит " + result.offset() + " сообщений"));
        }
    }


}
