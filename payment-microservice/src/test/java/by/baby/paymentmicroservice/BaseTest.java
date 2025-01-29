package by.baby.paymentmicroservice;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;



@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
public class BaseTest {

//    @Container
//    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest")
//            .asCompatibleSubstituteFor("apache/kafka"))
//            .withExposedPorts(9092)
//            .waitingFor(Wait.forLogMessage(".*[KafkaServer id=\\d+].* started.*",1))
//            .waitingFor(Wait.forListeningPort())
//            .withStartupTimeout(Duration.ofSeconds(120));

    @Container
    static KafkaContainer kafkaContainer =
            new KafkaContainer(DockerImageName.parse("apache/kafka:latest"));

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        Integer mappedPort = kafkaContainer.getMappedPort(9092);
        System.out.println(mappedPort);
        registry.add("spring.kafka.bootstrap-servers", () -> String.format("localhost:%d", mappedPort));
    }

}
