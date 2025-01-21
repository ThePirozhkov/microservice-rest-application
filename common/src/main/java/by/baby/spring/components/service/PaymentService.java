package by.baby.spring.components.service;

import by.baby.dto.PaymentDto;
import by.baby.exception.UnableToUpdateException;
import by.baby.spring.components.mapper.PaymentDtoMapper;
import by.baby.spring.components.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService implements by.baby.spring.components.service.Service<PaymentDto, Long> {

    private final PaymentRepository paymentRepository;
    private final PaymentDtoMapper paymentDtoMapper;

    @Override
    public List<PaymentDto> findAll() {
        return paymentRepository.findAll().stream()
                .map(paymentDtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PaymentDto> findById(Long id) {
        return paymentRepository.findById(id)
                .map(paymentDtoMapper::mapToDto);
    }

    @Override
    public PaymentDto save(PaymentDto dto) {
        return paymentDtoMapper.mapToDto(paymentRepository.save(paymentDtoMapper.mapToEntity(dto)));
    }

    @Override
    public PaymentDto update(PaymentDto dto, Long id) {
        return paymentRepository.findById(id)
                .map(paymentEntity -> {
                    paymentEntity.setFromUser(paymentEntity.getFromUser());
                    paymentEntity.setToUser(paymentEntity.getToUser());
                    paymentEntity.setAmount(dto.getAmount());
                    paymentEntity.setPaymentDate(dto.getPaymentDate());
                    return paymentEntity;
                })
                .map(paymentRepository::save)
                .map(paymentDtoMapper::mapToDto)
                .orElseThrow(() -> new UnableToUpdateException("Unable to update payment"));
    }

    @Override
    public void deleteById(Long id) {
        paymentRepository.deleteById(id);
    }
}
