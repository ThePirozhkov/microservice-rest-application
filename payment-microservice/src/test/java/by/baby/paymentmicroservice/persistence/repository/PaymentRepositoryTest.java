package by.baby.paymentmicroservice.persistence.repository;

import by.baby.persistence.entity.PaymentEntity;
import by.baby.persistence.entity.UserEntity;
import by.baby.persistence.repository.PaymentRepository;
import by.baby.persistence.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@DataJpaTest
@Transactional
@ActiveProfiles("test")
public class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldCreateSaveAndFindPaymentSuccessfully() {
        UserEntity user1 = new UserEntity();
        user1.setUsername("username");
        user1.setAuthToken("authToken");
        user1.setMoney(0L);
        userRepository.save(user1);
        UserEntity user2 = new UserEntity();
        user2.setUsername("username2");
        user2.setAuthToken("authToken2");
        user2.setMoney(0L);
        userRepository.save(user2);
        PaymentEntity testPayment = new PaymentEntity();
        testPayment.setFromUser(user1);
        testPayment.setToUser(user2);
        testPayment.setAmount(new BigDecimal(1000L));
        paymentRepository.saveAndFlush(testPayment);
        PaymentEntity retrievedPayment = paymentRepository.findById(testPayment.getId())
                .orElseThrow(RuntimeException::new);
        Assertions.assertEquals(testPayment, retrievedPayment);
    }
}
