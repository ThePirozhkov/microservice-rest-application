package by.baby.spring.components.service;

import by.baby.dto.PaymentDto;
import by.baby.exception.NotFoundException;
import by.baby.spring.components.mapper.PaymentDtoMapper;
import by.baby.spring.components.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService implements by.baby.spring.components.service.Service<PaymentDto, String> {

    private final PaymentRepository paymentRepository;
    private final PaymentDtoMapper paymentDtoMapper;

    @Override
    public List<PaymentDto> findAll() {
        return paymentRepository.findAll().stream()
                .map(paymentDtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PaymentDto> findById(String id) {
        return paymentRepository.findById(id)
                .map(paymentDtoMapper::mapToDto);
    }

    @Override
    public Optional<PaymentDto> save(PaymentDto dto) {
        return Optional.of(paymentDtoMapper.mapToDto(paymentRepository.save(paymentDtoMapper.mapToEntity(dto))));
    }

    @Override
    public Optional<PaymentDto> update(PaymentDto dto, String id) {
        log.info("Updating payment with id {} and dto {}", id, dto);
        Optional<PaymentDto> newDto = paymentRepository.findById(id)
                .map(paymentEntity -> {
                    if (paymentEntity.getAmount() != null)
                        paymentEntity.setAmount(dto.getAmount());
                    if (paymentEntity.getPaymentDate() != null)
                        paymentEntity.setPaymentDate(dto.getPaymentDate());
                    return paymentEntity;
                })
                .map(paymentRepository::save)
                .map(paymentDtoMapper::mapToDto);
        log.info("Updated payment with id {} and dto {}", id, newDto);
        return newDto;
    }

    @Override
    public boolean deleteById(String id) {
        if (!paymentRepository.existsById(id)) {
            throw new NotFoundException("Payment with id " + id + " not found");
        }
        paymentRepository.deleteById(id);
        return paymentRepository.existsById(id);
    }
}
