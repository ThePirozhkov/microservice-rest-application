package by.baby.paymentmicroservice.mapper;

import by.baby.dto.PaymentDto;
import by.baby.dto.UserDto;
import by.baby.entity.PaymentEntity;
import by.baby.entity.UserEntity;
import by.baby.spring.components.mapper.PaymentDtoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
public class PaymentDtoMapperTest {

    @Autowired
    private PaymentDtoMapper paymentDtoMapper;

    @Test
    public void shouldMapDtoToEntitySuccessfully() {
        PaymentDto paymentDto = new PaymentDto(
                1L,
                new UserDto(
                        1L, "username", "authToken", 0L, new Date()
                ),
                new UserDto(
                        2L, "username2", "authToken2", 0L, new Date()
                ),
                new BigDecimal(100L),
                new Date()
        );
        assertThat(paymentDtoMapper.mapToEntity(paymentDto)).isNotNull();
    }

    @Test
    public void shouldMapEntityToDtoSuccessfully() {
        UserEntity userEntity1 = new UserEntity();
        userEntity1.setId(1L);
        userEntity1.setUsername("username1");
        userEntity1.setAuthToken("authToken1");
        userEntity1.setMoney(0L);
        userEntity1.setCreatedAt(new Date());
        UserEntity userEntity2 = new UserEntity();
        userEntity2.setId(2L);
        userEntity2.setUsername("username2");
        userEntity2.setAuthToken("authToken2");
        userEntity2.setMoney(0L);
        userEntity2.setCreatedAt(new Date());

        PaymentEntity paymentEntity = new PaymentEntity();
        paymentEntity.setId(1L);
        paymentEntity.setFromUser(userEntity1);
        paymentEntity.setToUser(userEntity2);
        paymentEntity.setAmount(new BigDecimal(100L));
        paymentEntity.setPaymentDate(new Date());
        assertThat(paymentDtoMapper.mapToDto(paymentEntity)).isNotNull();
    }

    @Test
    public void shouldMapEntityToDtoWithNullAndGetRuntimeExceptionSuccessfully() {
        assertThatThrownBy(() -> paymentDtoMapper.mapToDto(null)).isInstanceOf(RuntimeException.class);
    }

}
