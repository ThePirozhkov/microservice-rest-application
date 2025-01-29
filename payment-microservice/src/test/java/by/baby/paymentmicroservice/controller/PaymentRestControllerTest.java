package by.baby.paymentmicroservice.controller;

import by.baby.dto.CreatedPaymentDto;
import by.baby.entity.PaymentEntity;
import by.baby.entity.UserEntity;
import by.baby.paymentmicroservice.BaseTest;
import by.baby.spring.components.repository.PaymentRepository;
import by.baby.spring.components.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class PaymentRestControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private final UserEntity user1 = new UserEntity();
    private final UserEntity user2 = new UserEntity();
    private final PaymentEntity payment1 = new PaymentEntity();
    private final PaymentEntity payment2 = new PaymentEntity();

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ObjectMapper objectMapper;

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
    }
}
