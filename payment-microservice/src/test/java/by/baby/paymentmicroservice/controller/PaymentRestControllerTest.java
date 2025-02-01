package by.baby.paymentmicroservice.controller;

import by.baby.dto.CreatedPaymentDto;
import by.baby.entity.PaymentEntity;
import by.baby.entity.UserEntity;
import by.baby.spring.components.repository.PaymentRepository;
import by.baby.spring.components.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class PaymentRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private static final KafkaContainer kafkaContainer =
            new KafkaContainer(DockerImageName.parse("apache/kafka"));

    private static final String TOPIC = "payment-created-events-topic";
    private static AdminClient adminClient;

    @Autowired
    private KafkaConsumer<String, String> consumer;

    private final UserEntity user1 = new UserEntity();
    private final UserEntity user2 = new UserEntity();
    private final PaymentEntity payment1 = new PaymentEntity();
    private final PaymentEntity payment2 = new PaymentEntity();

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @org.springframework.boot.test.context.TestConfiguration
    static class TestConfiguration {
        @Bean
        public Map<String, Object> kafkaConsumerConfigs() {
            Map<String, Object> props = new HashMap<>();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
            props.put(ConsumerConfig.GROUP_ID_CONFIG, "payment-created-events");
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
            return props;
        }

        @Bean
        public KafkaConsumer<String, String> kafkaConsumer() {
            return new KafkaConsumer<>(kafkaConsumerConfigs());
        }
    }

    @SneakyThrows
    @BeforeAll
    public static void setup1() {
        kafkaContainer.start();

        waitForBrokerReady();

        adminClient = AdminClient.create(Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers()));

            NewTopic topic = new NewTopic(TOPIC, 3, (short) 1);
            adminClient.createTopics(List.of(topic)).all().get();

        waitForTopicReady();
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

    private static void waitForTopicReady() {
        await().atMost(15, TimeUnit.SECONDS).until(() -> {
            log.info(adminClient.listTopics().names().get().toString());
            return adminClient.listTopics().names().get().contains(TOPIC);
        });
    }

    @DynamicPropertySource
    public static void kafkaProperties(@NotNull DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
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

    @BeforeEach
    public void setup2() {
        userRepository.deleteAll();
        paymentRepository.deleteAll();

        this.user1.setUsername("user1");
        this.user1.setAuthToken("authtoken1");
        this.user1.setMoney(0L);
        this.user1.setCreatedAt(Instant.now());
        userRepository.saveAndFlush(user1);
        this.user2.setUsername("user2");
        this.user2.setAuthToken("authtoken2");
        this.user2.setMoney(0L);
        this.user2.setCreatedAt(Instant.now());
        userRepository.saveAndFlush(user2);

        this.payment1.setId(UUID.randomUUID().toString());
        this.payment1.setToUser(user1);
        this.payment1.setFromUser(user2);
        this.payment1.setAmount(new BigDecimal(1000L));
        paymentRepository.saveAndFlush(payment1);
        this.payment2.setId(UUID.randomUUID().toString());
        this.payment2.setToUser(user2);
        this.payment2.setFromUser(user1);
        this.payment2.setAmount(new BigDecimal(2000L));
        paymentRepository.saveAndFlush(payment2);
    }

    @SneakyThrows
    @Test
    public void shouldGetAllPaymentsSuccessfully() {
        mockMvc.perform(MockMvcRequestBuilders.get("/payment")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.returned.length()").value(2));
    }

    @SneakyThrows
    @Test
    public void shouldGetPaymentSuccessfully() {
        mockMvc.perform(MockMvcRequestBuilders.get("/payment/" + payment1.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.returned.id").value(payment1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.returned.fromUser.id").value(payment1.getFromUser().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.returned.toUser.id").value(payment1.getToUser().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.returned.amount").value(payment1.getAmount()));
    }

    @SneakyThrows
    @Test
    public void shouldGetNotFoundPaymentException() {
        mockMvc.perform(MockMvcRequestBuilders.get("/payment/" + UUID.randomUUID() + "exception")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(MockMvcResultMatchers.status().isNotFound());
    }


    //TODO тест не дописан нужны тесты TransferService и интеграционный тест Kafka!
    @SneakyThrows
    @Test
    public void shouldCreatePaymentSuccessfully() {
        CreatedPaymentDto createdPaymentDto = new CreatedPaymentDto(
                user1.getId(), user2.getId(), new BigDecimal(1488L)
        );
        String jsonUpdDto = objectMapper.writeValueAsString(createdPaymentDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUpdDto))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        DescribeTopicsResult topicsResult = adminClient.describeTopics(Collections.singletonList(TOPIC));
        TopicDescription topicDescription = topicsResult.allTopicNames().get().get(TOPIC);

        Map<TopicPartition, OffsetSpec> request = new HashMap<>();
        for (TopicPartitionInfo partitionInfo : topicDescription.partitions()) {
            request.put(new TopicPartition(TOPIC, partitionInfo.partition()), OffsetSpec.latest());
        }

        checkTopic();

        ListOffsetsResult offsetsResult = adminClient.listOffsets(request);
        offsetsResult.all().get().forEach((tp, result) ->
                log.info("Партиция {} содержит {} сообщений", tp.partition(), result.offset()));

    }
}
