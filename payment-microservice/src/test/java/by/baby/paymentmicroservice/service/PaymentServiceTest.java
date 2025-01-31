package by.baby.paymentmicroservice.service;

import by.baby.dto.PaymentDto;
import by.baby.entity.PaymentEntity;
import by.baby.entity.UserEntity;
import by.baby.paymentmicroservice.BaseTest;
import by.baby.spring.components.mapper.UserDtoMapper;
import by.baby.spring.components.repository.PaymentRepository;
import by.baby.spring.components.repository.UserRepository;
import by.baby.spring.components.service.PaymentService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext
public class PaymentServiceTest extends BaseTest {

    @Autowired
    private UserRepository userRepository;

    private final UserEntity user1 = new UserEntity();
    private final UserEntity user2 = new UserEntity();
    private final PaymentEntity payment1 = new PaymentEntity();
    private final PaymentEntity payment2 = new PaymentEntity();
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private UserDtoMapper userDtoMapper;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
        user1.setUsername("user1");
        user1.setAuthToken("authtoken1");
        user1.setMoney(0L);
        user1.setCreatedAt(Instant.now());
        userRepository.saveAndFlush(user1);
        user2.setUsername("user2");
        user2.setAuthToken("authtoken2");
        user2.setMoney(0L);
        user2.setCreatedAt(Instant.now());
        userRepository.saveAndFlush(user2);
        payment1.setId(UUID.randomUUID() + "-1");
        payment1.setFromUser(user1);
        payment1.setToUser(user2);
        payment1.setAmount(new BigDecimal("100.00"));
        payment2.setId(UUID.randomUUID() + "-2");
        payment2.setFromUser(user2);
        payment2.setToUser(user1);
        payment2.setAmount(new BigDecimal("100.00"));
        paymentRepository.saveAndFlush(payment1);
        paymentRepository.saveAndFlush(payment2);
    }

    @Order(1)
    @Test
    public void shouldFindAllSuccessfully() {
        assertThat(paymentService.findAll()).hasSize(2);
    }

    @Order(2)
    @Test
    public void shouldFindByIdSuccessfully() {
        assertThat(paymentService.findById(payment1.getId())).isNotEmpty();
    }

    @SneakyThrows
    @Order(3)
    @Test
    public void shouldUpdatePaymentSuccessfully() {
        PaymentDto updDto = new PaymentDto();
        updDto.setAmount(new BigDecimal("100.99"));
        updDto.setPaymentDate(Instant.MAX);

        PaymentDto expDto = new PaymentDto();
        expDto.setId(payment1.getId());
        expDto.setFromUser(userDtoMapper.mapToDto(payment1.getFromUser()));
        expDto.setToUser(userDtoMapper.mapToDto(payment1.getToUser()));
        expDto.setAmount(updDto.getAmount());
        expDto.setPaymentDate(updDto.getPaymentDate());

        paymentService.update(updDto, payment1.getId());
        assertThat(paymentService.findById(payment1.getId()).get())
                .usingRecursiveComparison()
                .isEqualTo(expDto);
    }

    @Order(4)
    @Test
    public void shouldDeletePaymentSuccessfully() {
        if (paymentRepository.existsById(payment1.getId())) {
            paymentRepository.deleteById(payment1.getId());
            assertThat(paymentRepository.findById(payment1.getId())).isEmpty();
        } else {
            throw new RuntimeException();
        }
    }
}
