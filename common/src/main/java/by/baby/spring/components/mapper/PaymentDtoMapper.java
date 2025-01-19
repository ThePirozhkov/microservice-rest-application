package by.baby.spring.components.mapper;

import by.baby.dto.PaymentDto;
import by.baby.entity.PaymentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentDtoMapper implements Mapper<PaymentDto, PaymentEntity> {

    private final UserDtoMapper userDtoMapper;

    @Override
    public PaymentDto mapToDto(PaymentEntity value) {
        return Optional.ofNullable(value)
                .map(payment -> new PaymentDto(
                        payment.getId(),
                        userDtoMapper.mapToDto(payment.getFromUser()),
                        userDtoMapper.mapToDto(payment.getToUser()),
                        payment.getAmount(),
                        payment.getPaymentDate()
                ))
                .orElseThrow(() -> new RuntimeException("Can't map to PaymentDto"));
    }

    @Override
    public PaymentEntity mapToEntity(PaymentDto value) {
        if (value == null) {
            throw new RuntimeException("Can't map to PaymentEntity");
        }
        PaymentEntity payment = new PaymentEntity();
        payment.setId(value.getId());
        payment.setFromUser(userDtoMapper.mapToEntity(value.getFromUser()));
        payment.setToUser(userDtoMapper.mapToEntity(value.getToUser()));
        payment.setAmount(value.getAmount());
        payment.setPaymentDate(value.getPaymentDate());
        return payment;
    }
}
